package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.complexity;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElementVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableContent;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

public class RobotNestedVariableInspection extends LocalInspectionTool implements DumbAware {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new RobotVisitor() {

            @Override
            public void visitVariable(@NotNull RobotVariable o) {
                if (o.getParent() instanceof RobotVariableContent) {
                    holder.registerProblem(o, RobotBundle.message("INSP.variable.nested"));
                }
            }
        };
    }
}
