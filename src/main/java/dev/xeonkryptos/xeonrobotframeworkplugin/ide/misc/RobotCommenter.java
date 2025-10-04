package dev.xeonkryptos.xeonrobotframeworkplugin.ide.misc;

import com.intellij.codeInsight.generation.IndentedCommenter;
import com.intellij.lang.CodeDocumentationAwareCommenter;
import com.intellij.psi.PsiComment;
import com.intellij.psi.tree.IElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import org.jetbrains.annotations.Nullable;

public class RobotCommenter implements CodeDocumentationAwareCommenter, IndentedCommenter {

   @Nullable
   @Override
   public String getLineCommentPrefix() {
      return "# ";
   }

   @Nullable
   @Override
   public String getBlockCommentPrefix() {
      return null;
   }

   @Nullable
   @Override
   public String getBlockCommentSuffix() {
      return null;
   }

   @Nullable
   @Override
   public String getCommentedBlockCommentPrefix() {
      return null;
   }

   @Nullable
   @Override
   public String getCommentedBlockCommentSuffix() {
      return null;
   }

    @Override
    public @Nullable IElementType getLineCommentTokenType() {
        return RobotTypes.COMMENT;
    }

    @Override
    public @Nullable IElementType getBlockCommentTokenType() {
        return null;
    }

    @Override
    public @Nullable IElementType getDocumentationCommentTokenType() {
        return null;
    }

    @Override
    public @Nullable String getDocumentationCommentPrefix() {
        return null;
    }

    @Override
    public @Nullable String getDocumentationCommentLinePrefix() {
        return null;
    }

    @Override
    public @Nullable String getDocumentationCommentSuffix() {
        return null;
    }

    @Override
    public boolean isDocumentationComment(PsiComment element) {
        return false;
    }

    @Nullable
    @Override
    public Boolean forceIndentedLineComment() {
        return true;
    }
}
