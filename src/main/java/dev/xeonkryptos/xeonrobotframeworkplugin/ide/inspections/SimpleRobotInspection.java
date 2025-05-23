package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections;

import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleRobotInspection extends LocalInspectionTool implements SimpleInspection {

   @Nls
   @NotNull
   @Override
   public String getGroupDisplayName() {
       return RobotBundle.getMessage(this.getGroupNameKey());
   }

   @NotNull
   protected abstract String getGroupNameKey();

   @NotNull
   @Override
   public String getShortName() {
       return this.getClass().getSimpleName();
   }

   @Override
   public boolean isEnabledByDefault() {
      return true;
   }

   @NotNull
   @Override
   public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
      return new SimpleInspectionVisitor(holder, this);
   }
}
