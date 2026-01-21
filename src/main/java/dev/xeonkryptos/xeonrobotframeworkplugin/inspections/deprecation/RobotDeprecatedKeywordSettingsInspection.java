package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.deprecation;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.RobotVersionBasedInspection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSettingId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider.RobotVersion;
import org.jetbrains.annotations.NotNull;

public class RobotDeprecatedKeywordSettingsInspection extends RobotVersionBasedInspection implements DumbAware {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new RobotVisitor() {
            @Override
            public void visitLocalSetting(@NotNull RobotLocalSetting o) {
                String settingName = o.getSettingName();
                RobotUserKeywordStatement userKeywordStatement = PsiTreeUtil.getParentOfType(o, RobotUserKeywordStatement.class, true);
                if (settingName.equalsIgnoreCase(RobotNames.RETURN_LOCAL_SETTING_NAME) && userKeywordStatement != null) {
                    RobotLocalSettingId localSettingId = o.getLocalSettingId();
                    holder.registerProblem(localSettingId, RobotBundle.message("INSP.setting.local.return.deprecated"), ProblemHighlightType.LIKE_DEPRECATED);
                }
            }
        };
    }

    @Override
    protected RobotVersion getMinimumRobotVersion() {
        return new RobotVersion(7, 0, 0);
    }
}
