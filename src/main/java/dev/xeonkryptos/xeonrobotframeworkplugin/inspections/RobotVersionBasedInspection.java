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
    public void inspectionStarted(@NotNull LocalInspectionToolSession session, boolean isOnTheFly) {
        PsiFile file = session.getFile();
        Project project = file.getProject();
        RobotVersion robotVersion = RobotVersionProvider.getInstance(project).getRobotVersion(file);
        session.putUserData(ROBOT_VERSION_KEY, robotVersion);
    }

    protected final RobotVersion getRobotVersion(@NotNull LocalInspectionToolSession session) {
        return session.getUserData(ROBOT_VERSION_KEY);
    }
}
