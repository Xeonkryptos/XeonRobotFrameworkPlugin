package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.PatternUtil;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public abstract class RobotPsiElementBase extends ASTWrapperPsiElement implements RobotStatement {

   public RobotPsiElementBase(@NotNull ASTNode node) {
      super(node);
   }

   @NotNull
   protected static String getPresentableText(ASTNode node) {
      return PatternUtil.getPresentableText(node.getText());
   }

   @Override
   public ItemPresentation getPresentation() {
      return new ItemPresentation() {

         @Override
         public String getPresentableText() {
            return RobotPsiElementBase.this.getPresentableText();
         }

         @Override
         public String getLocationString() {
            return null;
         }

         @Override
         public Icon getIcon(boolean var1) {
            return RobotPsiElementBase.this.getIcon(ICON_FLAG_VISIBILITY);
         }
      };
   }

   @NotNull
   @Override
   public String getPresentableText() {
      return getPresentableText(this.getNode());
   }

   @NotNull
   @Override
   public String getName() {
      return this.getPresentableText();
   }

   public PsiElement setName(@NotNull String newName) {
      return this;
   }

   @Override
   public String toString() {
      return super.toString() + "(" + getPresentableText() + ")";
   }
}
