package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.RobotKeywordReference;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class KeywordPartImpl extends RobotPsiElementBase implements KeywordInvokable {

   public KeywordPartImpl(@NotNull ASTNode node) {
      super(node);
   }

   @Override
   public @NotNull Collection<Parameter> getParameters() {
      return Collections.emptySet();
   }

   @NotNull
   @Override
   public final Collection<PositionalArgument> getPositionalArguments() {
       return Collections.emptySet();
   }

   @Override
   public PsiReference getReference() {
      return new RobotKeywordReference(this);
   }

   @NotNull
   @Override
   public final String getPresentableText() {
      return getPresentableText(this.getNode().getTreeParent());
   }
}
