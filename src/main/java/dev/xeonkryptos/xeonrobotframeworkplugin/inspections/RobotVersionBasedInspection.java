package dev.xeonkryptos.xeonrobotframeworkplugin.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
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

    @Override
    public void inspectionStarted(@NotNull LocalInspectionToolSession session, boolean isOnTheFly) {
        PsiFile file = session.getFile();
        RobotVersion foundRobotVersion = file.getUserData(ROBOT_VERSION_KEY);
        if (foundRobotVersion == null) {
            foundRobotVersion = defineRobotVersion(file);
        }
        session.putUserData(ROBOT_VERSION_KEY, foundRobotVersion);
    }

    protected abstract RobotVersion getMinimumRobotVersion();

    private RobotVersion defineRobotVersion(PsiFile file) {
        Project project = file.getProject();
        return RobotVersionProvider.getInstance(project).getRobotVersion(file);
    }

    protected final RobotVersion getRobotVersion(@NotNull LocalInspectionToolSession session) {
        return session.getUserData(ROBOT_VERSION_KEY);
    }
}
