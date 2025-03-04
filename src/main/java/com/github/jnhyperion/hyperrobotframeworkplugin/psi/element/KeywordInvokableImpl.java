package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.RobotKeywordReference;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class KeywordInvokableImpl extends RobotPsiElementBase implements KeywordInvokable {

   public KeywordInvokableImpl(@NotNull ASTNode node) {
      super(node);
   }

   @NotNull
   @Override
   public Collection<Parameter> getParameters() {
      PsiElement parent = this.getParent();
      if (parent instanceof KeywordStatement) {
         return ((KeywordStatement) parent).getParameters();
      } else {
         return Collections.emptySet();
      }
   }

   @NotNull
   @Override
   public final Collection<PositionalArgument> getPositionalArguments() {
      PsiElement parent = this.getParent();
      if (parent instanceof KeywordStatement) {
          return ((KeywordStatement) parent).getPositionalArguments();
      } else {
          return Collections.emptySet();
      }
   }

   @Override
   public PsiReference getReference() {
      return new RobotKeywordReference(this);
   }
}
