package com.github.jnhyperion.hyperrobotframeworkplugin.ide;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.config.RobotOptionsProvider;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFile;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class RobotTypedActionHandler extends TypedHandlerDelegate {

    @NotNull
    @Override
    public Result beforeCharTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file, @NotNull FileType fileType) {
        try {
            if (file instanceof RobotFile && (c == '$' || c == '@' || c == '&') && RobotOptionsProvider.getInstance(project).smartAutoEncloseVariable()) {
                int offset = editor.getCaretModel().getOffset();
                String documentText = editor.getDocument().getText();
                String newText = documentText.substring(0, offset) + c + "{}" + documentText.substring(offset);
                editor.getDocument().setText(newText);
                editor.getCaretModel().moveToOffset(offset + 2);
                return Result.STOP;
            }
        } catch (Throwable ignored) {
        }
        return Result.CONTINUE;
    }
}
