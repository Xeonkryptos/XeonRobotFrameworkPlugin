package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.RobotKeywordReference;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;

public class KeywordInvokableImpl extends RobotPsiElementBase implements KeywordInvokable {

   public KeywordInvokableImpl(@NotNull ASTNode node) {
      super(node);
   }

   @NotNull
   @Override
   public final Collection<Argument> getArguments() {
      PsiElement parent = this.getParent();
      if (parent instanceof KeywordStatement) {
          return ((KeywordStatement) parent).getArguments();
      } else {
          return Collections.emptySet();
      }
   }

   @Override
   public PsiReference getReference() {
      return new RobotKeywordReference(this);
   }
}
