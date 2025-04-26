package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.PatternUtil;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public abstract class RobotPsiElementBase extends ASTWrapperPsiElement implements RobotStatement {

   private String name;

   public RobotPsiElementBase(@NotNull ASTNode node) {
      super(node);
   }

   @NotNull
   public static String getPresentableText(String text) {
      return PatternUtil.getPresentableText(text);
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
         public Icon getIcon(boolean unused) {
            return RobotPsiElementBase.this.getIcon(ICON_FLAG_VISIBILITY);
         }
      };
   }

   @NotNull
   @Override
   public String getName() {
      if (name == null) {
         name = getPresentableText();
      }
      return name;
   }

   @NotNull
   @Override
   public String getPresentableText() {
      String unescapedText = InjectedLanguageManager.getInstance(getProject()).getUnescapedText(this);
      return getPresentableText(unescapedText);
   }

   @Override
   public void subtreeChanged() {
      super.subtreeChanged();

      name = null;
   }

   public PsiElement setName(@NotNull String newName) {
      return this;
   }

   @Override
   public String toString() {
      return super.toString() + "(" + getPresentableText() + ")";
   }
}
