package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class SettingImpl extends RobotPsiElementBase implements Setting {

   public SettingImpl(@NotNull ASTNode node) {
      super(node);
   }

   @Override
   public final boolean isSuiteTeardown() {
      return "Suite Teardown".equalsIgnoreCase(this.getPresentableText());
   }

   @Override
   public final boolean isTestTeardown() {
      return "Test Teardown".equalsIgnoreCase(this.getPresentableText());
   }
}
