// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package dev.xeonkryptos.xeonrobotframeworkplugin.ide.rename;

import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.rename.inplace.InplaceRefactoring;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenameHandler;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenamer;
import com.jetbrains.python.psi.PyFunction;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class RobotMemberInplaceRenameHandler extends MemberInplaceRenameHandler {

    static final Key<PsiNamedElement> SOURCE_STATEMENT_KEY = Key.create("SOURCE_STATEMENT_KEY");

    @Override
    protected boolean isAvailable(@Nullable PsiElement element, @NotNull Editor editor, @NotNull PsiFile file) {
        FileType fileType = file.getFileType();
        if (fileType != RobotFeatureFileType.getInstance() && fileType != RobotResourceFileType.getInstance()) {
            return false;
        }

        PsiElement nameSuggestionContext = file.findElementAt(editor.getCaretModel().getOffset());
        if (nameSuggestionContext == null && editor.getCaretModel().getOffset() > 0) {
            nameSuggestionContext = file.findElementAt(editor.getCaretModel().getOffset() - 1);
        }

        if (element == null && LookupManager.getActiveLookup(editor) != null) {
            element = PsiTreeUtil.getParentOfType(nameSuggestionContext, PsiNamedElement.class);
        }
        if (element == null) {
            return false;
        }
        if (element instanceof PyFunction && nameSuggestionContext != null && (nameSuggestionContext.getContext() instanceof RobotKeywordCallName
                                                                               || nameSuggestionContext.getContext() instanceof RobotKeywordCall)) {
            RobotKeywordCall keywordCall = PsiTreeUtil.getParentOfType(nameSuggestionContext, RobotKeywordCall.class);
            if (keywordCall != null) {
                element.putUserData(SOURCE_STATEMENT_KEY, keywordCall);
                return editor.getSettings().isVariableInplaceRenameEnabled();
            }
        }
        return editor.getSettings().isVariableInplaceRenameEnabled() && element instanceof PsiNameIdentifierOwner && element instanceof RobotStatement
               && !(element instanceof RobotVariableDefinition);
    }

    @NotNull
    @Override
    protected MemberInplaceRenamer createMemberRenamer(@NotNull PsiElement element, @NotNull PsiNameIdentifierOwner elementToRename, @NotNull Editor editor) {
        PsiNamedElement sourceStatement = element.getUserData(SOURCE_STATEMENT_KEY);
        PsiNamedElement psiNamedElement = Objects.requireNonNullElse(sourceStatement, elementToRename);
        return new MemberInplaceRenamer(psiNamedElement, element, editor);
    }

    @Override
    public InplaceRefactoring doRename(@NotNull PsiElement elementToRename, @NotNull Editor editor, @Nullable DataContext dataContext) {
        PsiNamedElement sourceStatement = elementToRename.getUserData(SOURCE_STATEMENT_KEY);
        elementToRename = Objects.requireNonNullElse(sourceStatement, elementToRename);
        return super.doRename(elementToRename, editor, dataContext);
    }
}
