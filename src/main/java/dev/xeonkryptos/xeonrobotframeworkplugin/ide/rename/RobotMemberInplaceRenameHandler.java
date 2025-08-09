// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package dev.xeonkryptos.xeonrobotframeworkplugin.ide.rename;

import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenameHandler;
import com.intellij.refactoring.rename.inplace.MemberInplaceRenamer;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotMemberInplaceRenameHandler extends MemberInplaceRenameHandler {



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

        return editor.getSettings().isVariableInplaceRenameEnabled() && element instanceof PsiNameIdentifierOwner && element instanceof RobotStatement
               || element instanceof PyFunction;
    }

    @Override
    protected @NotNull MemberInplaceRenamer createMemberRenamer(@NotNull PsiElement element,
                                                                @NotNull PsiNameIdentifierOwner elementToRename,
                                                                @NotNull Editor editor) {
        if (element instanceof PyClass elementClass && elementToRename instanceof PyFunction function && function.getContainingClass() == element
            && PyUtil.isInitOrNewMethod(elementToRename)) {
            return new MemberInplaceRenamer(elementClass, element, editor);
        }
        return new MemberInplaceRenamer(elementToRename, element, editor);
    }
}
