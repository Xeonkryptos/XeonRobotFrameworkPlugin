package com.github.jnhyperion.hyperrobotframeworkplugin.psi;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

public class RobotLanguage extends Language {

   public static final RobotLanguage INSTANCE = new RobotLanguage();

   private RobotLanguage() {
      super("Robot", "");
   }

   @NotNull
   public String getDisplayName() {
      return "Robot";
   }
}
