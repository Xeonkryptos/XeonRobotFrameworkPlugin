package com.github.jnhyperion.hyperrobotframeworkplugin.ide;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.config.RobotOptionsProvider;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFile;
import com.intellij.codeInsight.completion.NextPrevParameterHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import org.jetbrains.annotations.NotNull;

public class RobotTabActionHandler extends NextPrevParameterHandler {

   public RobotTabActionHandler(EditorActionHandler originalHandler) {
      super(originalHandler, true);
   }

   @Override
   public void executeWriteAction(@NotNull Editor editor, @NotNull Caret caret, DataContext dataContext) {
       Project project = editor.getProject();
       if (project != null
           && RobotOptionsProvider.getInstance(project).reformatOnSave()
           && PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument()) instanceof RobotFile) {
           int offset = caret.getOffset();
           String documentText = editor.getDocument().getText();
           String newText = documentText.substring(0, offset) + "    " + documentText.substring(offset);
           editor.getDocument().setText(newText);
           caret.moveToOffset(offset + 4);
       } else {
           super.executeWriteAction(editor, caret, dataContext);
       }
   }
}
