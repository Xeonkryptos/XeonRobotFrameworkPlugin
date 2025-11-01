package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.deprecation;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElementVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.RobotVersionBasedInspection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider.RobotVersion;
import org.jetbrains.annotations.NotNull;

public class RobotDeprecatedSectionsNamingInspection extends RobotVersionBasedInspection implements DumbAware {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new RobotVisitor() {

            @Override
            public void visitSection(@NotNull RobotSection o) {
                super.visitSection(o);
                RobotVersion robotVersion = getRobotVersion(session);
                if (robotVersion != null && robotVersion.supports(new RobotVersion(6, 0, 0))) {
                    String sectionName = o.getText().replace("*", "").trim();
                    if ("Test Case".equalsIgnoreCase(sectionName) || "Keyword".equalsIgnoreCase(sectionName) || "Setting".equalsIgnoreCase(sectionName)
                        || "Variable".equalsIgnoreCase(sectionName) || "Comment".equalsIgnoreCase(sectionName) || "Task".equalsIgnoreCase(sectionName)) {
                        holder.registerProblem(o, RobotBundle.message("INSP.section.single-section-name.deprecated"), ProblemHighlightType.LIKE_DEPRECATED);
                    }
                }
            }
        };
    }
}
