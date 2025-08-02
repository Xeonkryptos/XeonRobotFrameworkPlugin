package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.complexity;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

public class RobotNestedVariable extends LocalInspectionTool {

    @Override
    public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
        return new RobotVisitor() {

            @Override
            public void visitVariable(@NotNull RobotVariable o) {
                super.visitVariable(o);
                RobotVariableBodyId nameIdentifier = o.getNameIdentifier();
                if (nameIdentifier == null) {
                    holder.registerProblem(o, RobotBundle.getMessage("INSP.variable.nested"));
                }
            }
        };
    }
}
