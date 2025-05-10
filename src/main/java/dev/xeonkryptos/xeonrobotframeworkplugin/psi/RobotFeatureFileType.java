package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import com.intellij.openapi.fileTypes.LanguageFileType;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotFeatureFileType extends LanguageFileType {

   private static final RobotFeatureFileType INSTANCE = new RobotFeatureFileType();

   private RobotFeatureFileType() {
      super(RobotLanguage.INSTANCE);
   }

   public static RobotFeatureFileType getInstance() {
      return INSTANCE;
   }

   @NotNull
   public String getName() {
      return "Robot Feature";
   }

   @NotNull
   public String getDescription() {
      return "Robot feature files";
   }

   @NotNull
   public String getDefaultExtension() {
      return "robot";
   }

   @Nullable
   public Icon getIcon() {
      return RobotIcons.FILE;
   }

   @NotNull
   public String getDisplayName() {
       return this.getName();
   }
}
