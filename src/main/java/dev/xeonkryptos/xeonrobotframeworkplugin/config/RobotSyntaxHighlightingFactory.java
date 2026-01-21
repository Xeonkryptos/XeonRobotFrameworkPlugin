package dev.xeonkryptos.xeonrobotframeworkplugin.config;

import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotSyntaxHighlightingFactory extends SyntaxHighlighterFactory {

   @NotNull
   public SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile file) {
      return new RobotHighlighter();
   }
}
