package dev.xeonkryptos.xeonrobotframeworkplugin.ide.actionhandler;

import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class RobotTypedActionHandler extends TypedHandlerDelegate {

    @NotNull
    @Override
    public Result beforeCharTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file, @NotNull FileType fileType) {
        if (file instanceof RobotFile && RobotOptionsProvider.getInstance(project).smartAutoEncloseVariable()) {
            int offset = editor.getCaretModel().getOffset();
            Document document = editor.getDocument();
            if (offset > 0) {
                char firstCharBefore = document.getText(new TextRange(offset - 1, offset)).charAt(0);
                if (c == '{' && (firstCharBefore == '$' || firstCharBefore == '@' || firstCharBefore == '&')) {
                    if (offset < document.getTextLength() && document.getText(new TextRange(offset, offset + 1)).charAt(0) == c) {
                        editor.getCaretModel().moveToOffset(offset + 1);
                        return Result.STOP;
                    }
                    document.insertString(offset, "{}");
                    editor.getCaretModel().moveToOffset(offset + 1);
                    return Result.STOP;
                } else if (c == '}' && document.getText(new TextRange(offset, offset + 1)).charAt(0) == '}') {
                    editor.getCaretModel().moveToOffset(offset + 1);
                    return Result.STOP;
                }
            }
        }
        return Result.CONTINUE;
    }
}
