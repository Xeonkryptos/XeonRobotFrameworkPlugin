// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package dev.xeonkryptos.xeonrobotframeworkplugin.ide.rename;

import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenameHandler;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenamer;
import com.jetbrains.python.psi.PyFunction;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class RobotMemberInplaceRenameHandler extends MemberInplaceRenameHandler {

    public static final Key<PyFunction> REFERENCED_PY_FUNCTION_KEY = Key.create("REFERENCED_PY_FUNCTION_KEY");

    private static final Key<PsiNamedElement> SOURCE_STATEMENT_KEY = Key.create("SOURCE_STATEMENT_KEY");

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
        if (element instanceof PyFunction pyFunction && nameSuggestionContext != null && (nameSuggestionContext.getContext() instanceof RobotKeywordCallName
                                                                                          || nameSuggestionContext.getContext() instanceof RobotKeywordCall)) {
            RobotKeywordCall keywordCall = PsiTreeUtil.getParentOfType(nameSuggestionContext, RobotKeywordCall.class);
            element.putUserData(SOURCE_STATEMENT_KEY, keywordCall);
            element.putUserData(REFERENCED_PY_FUNCTION_KEY, pyFunction);
            return editor.getSettings().isVariableInplaceRenameEnabled();
        }
        return false;
    }

    @Override
    protected @NotNull MemberInplaceRenamer createMemberRenamer(@NotNull PsiElement element,
                                                                @NotNull PsiNameIdentifierOwner elementToRename,
                                                                @NotNull Editor editor) {
        PsiNamedElement sourceStatement = element.getUserData(SOURCE_STATEMENT_KEY);
        PsiNamedElement psiNamedElement = Objects.requireNonNullElse(sourceStatement, elementToRename);
        return new MemberInplaceRenamer(psiNamedElement, element, editor);
    }
}
