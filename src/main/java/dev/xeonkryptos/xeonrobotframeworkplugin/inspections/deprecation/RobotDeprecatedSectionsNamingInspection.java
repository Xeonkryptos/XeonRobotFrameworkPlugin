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
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider.RobotVersion;
import org.jetbrains.annotations.NotNull;

public class RobotDeprecatedSectionsNamingInspection extends RobotVersionBasedInspection implements DumbAware {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new RobotVisitor() {

            @Override
            public void visitSection(@NotNull RobotSection o) {
                String sectionName = o.getText().replace("*", "").trim();
                if (RobotNames.TEST_CASE_SECTION_NAME.equalsIgnoreCase(sectionName) || RobotNames.KEYWORD_SECTION_NAME.equalsIgnoreCase(sectionName)
                    || RobotNames.SETTING_SECTION_NAME.equalsIgnoreCase(sectionName) || RobotNames.VARIABLE_SECTION_NAME.equalsIgnoreCase(sectionName)
                    || RobotNames.COMMENT_SECTION_NAME.equalsIgnoreCase(sectionName) || RobotNames.TASK_SECTION_NAME.equalsIgnoreCase(sectionName)) {
                    holder.registerProblem(o, RobotBundle.message("INSP.section.single-section-name.deprecated"), ProblemHighlightType.LIKE_DEPRECATED);
                }
            }
        };
    }

    @Override
    protected RobotVersion getMinimumRobotVersion() {
        return new RobotVersion(6, 0, 0);
    }
}
