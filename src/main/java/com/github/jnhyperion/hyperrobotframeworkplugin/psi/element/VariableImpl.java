package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.RobotVariableReference;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

public class VariableImpl extends RobotPsiElementBase implements Variable {

   public VariableImpl(@NotNull ASTNode node) {
         super(node);
   }

   public PsiReference getReference() {
      return new RobotVariableReference(this);
   }

   @Override
   public final boolean isNested() {
      String text = this.getPresentableText();
      return StringUtil.getOccurrenceCount(text, "}") > 1
         && StringUtil.getOccurrenceCount(text, "${") + StringUtil.getOccurrenceCount(text, "@{") + StringUtil.getOccurrenceCount(text, "%{") > 1;
   }
}
