package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.deprecation;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElementVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.RobotVersionBasedInspection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTagsStatementGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider.RobotVersion;
import org.jetbrains.annotations.NotNull;

public class RobotDeprecatedGlobalSettingsInspection extends RobotVersionBasedInspection implements DumbAware {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new RobotVisitor() {
            @Override
            public void visitTagsStatementGlobalSetting(@NotNull RobotTagsStatementGlobalSetting o) {
                super.visitTagsStatementGlobalSetting(o);

                RobotVersion robotVersion = getRobotVersion(session);
                if (robotVersion.supports(new RobotVersion(6, 1, 0))) {
                    String settingName = o.getNameElement().getText();
                    if (settingName.equalsIgnoreCase("Default Tags")) {
                        holder.registerProblem(o.getNameElement(),
                                               RobotBundle.getMessage("INSP.setting.global.default-tags.deprecated"),
                                               ProblemHighlightType.LIKE_DEPRECATED);
                    } else if (settingName.equalsIgnoreCase("Force Tags")) {
                        holder.registerProblem(o.getNameElement(),
                                               RobotBundle.getMessage("INSP.setting.global.force-tags.deprecated"),
                                               ProblemHighlightType.LIKE_DEPRECATED);
                    }
                }
            }
        };
    }
}
