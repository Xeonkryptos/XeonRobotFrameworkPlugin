package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class BracketSettingImpl extends RobotPsiElementBase implements BracketSetting {

   private static final String ARGUMENTS = "[Arguments]";
   private static final String TEARDOWN = "[Teardown]";

   public BracketSettingImpl(@NotNull ASTNode node) {
      super(node);
   }

   @Override
   public final boolean isArguments() {
      return ARGUMENTS.equalsIgnoreCase(this.getPresentableText());
   }

   @Override
   public final boolean isTeardown() {
      return TEARDOWN.equalsIgnoreCase(this.getPresentableText());
   }
}
