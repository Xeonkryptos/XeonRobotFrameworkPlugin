package dev.xeonkryptos.xeonrobotframeworkplugin.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider.RobotVersion;
import org.jetbrains.annotations.NotNull;

public abstract class RobotVersionBasedInspection extends LocalInspectionTool {

    private static final Key<RobotVersion> ROBOT_VERSION_KEY = Key.create("ROBOT_VERSION_KEY");

    @Override
    public boolean isAvailableForFile(@NotNull PsiFile file) {
        RobotVersion robotVersion = defineRobotVersion(file);
        RobotVersion minimumRobotVersion = getMinimumRobotVersion();
        if (robotVersion != null) {
            file.putUserData(ROBOT_VERSION_KEY, robotVersion);
            return robotVersion.supports(minimumRobotVersion);
        }
        return false;
    }

    protected abstract RobotVersion getMinimumRobotVersion();

    private RobotVersion defineRobotVersion(PsiFile file) {
        Project project = file.getProject();
        return RobotVersionProvider.getInstance(project).getRobotVersion(file);
    }
}
