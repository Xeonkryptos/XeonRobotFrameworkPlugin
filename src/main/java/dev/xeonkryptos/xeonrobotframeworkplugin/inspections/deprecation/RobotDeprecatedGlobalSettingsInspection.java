package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.deprecation;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElementVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.RobotVersionBasedInspection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTagsStatementGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider.RobotVersion;
import org.jetbrains.annotations.NotNull;

public class RobotDeprecatedGlobalSettingsInspection extends RobotVersionBasedInspection implements DumbAware {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new RobotVisitor() {
            @Override
            public void visitTagsStatementGlobalSetting(@NotNull RobotTagsStatementGlobalSetting o) {
                String settingName = o.getSettingName();
                if (settingName.equalsIgnoreCase(RobotNames.DEFAULT_TAGS_LOCAL_SETTING_NAME)) {
                    holder.registerProblem(o.getNameElement(), RobotBundle.message("INSP.setting.global.default-tags.deprecated"), ProblemHighlightType.LIKE_DEPRECATED);
                } else if (settingName.equalsIgnoreCase(RobotNames.FORCE_TAGS_LOCAL_SETTING_NAME)) {
                    holder.registerProblem(o.getNameElement(), RobotBundle.message("INSP.setting.global.force-tags.deprecated"), ProblemHighlightType.LIKE_DEPRECATED);
                }
            }
        };
    }

    @Override
    protected RobotVersion getMinimumRobotVersion() {
        return new RobotVersion(6, 1, 0);
    }
}
