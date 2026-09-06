package dev.xeonkryptos.xeonrobotframeworkplugin.inspections;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider.RobotVersion;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public abstract class RobotVersionBasedInspection extends LocalInspectionTool {

    private static final Key<TimedRobotVersion> ROBOT_VERSION_KEY = Key.create("ROBOT_VERSION_KEY");

    @Override
    public boolean isAvailableForFile(@NotNull PsiFile file) {
        TimedRobotVersion robotVersion = file.getUserData(ROBOT_VERSION_KEY);
        RobotVersion minimumRobotVersion = getMinimumRobotVersion();
        if (robotVersion == null || robotVersion.isExpired()) {
            robotVersion = getRobotVersion(file);
        }
        if (robotVersion != null) {
            file.putUserData(ROBOT_VERSION_KEY, robotVersion);
            return robotVersion.robotVersion().supports(minimumRobotVersion);
        }
        return false;
    }

    @Override
    public void inspectionStarted(@NotNull LocalInspectionToolSession session, boolean isOnTheFly) {
        PsiFile file = session.getFile();
        TimedRobotVersion foundRobotVersion = file.getUserData(ROBOT_VERSION_KEY);
        if (foundRobotVersion == null || foundRobotVersion.isExpired()) {
            foundRobotVersion = getRobotVersion(file);
        }
        session.putUserData(ROBOT_VERSION_KEY, foundRobotVersion);
    }

    protected abstract RobotVersion getMinimumRobotVersion();

    private TimedRobotVersion getRobotVersion(PsiFile file) {
        Project project = file.getProject();
        RobotVersion robotVersion = RobotVersionProvider.getInstance(project).getRobotVersion();
        if (robotVersion == null) {
            return null;
        }
        return new TimedRobotVersion(robotVersion);
    }

    protected final RobotVersion getRobotVersion(@NotNull LocalInspectionToolSession session) {
        TimedRobotVersion timedRobotVersion = session.getUserData(ROBOT_VERSION_KEY);
        if (timedRobotVersion != null) {
            return timedRobotVersion.robotVersion;
        }
        return null;
    }

    record TimedRobotVersion(RobotVersion robotVersion, long timestamp) {

        private static final long EXPIRATION_MILLIS = TimeUnit.MINUTES.toMillis(5);

        public TimedRobotVersion(RobotVersion robotVersion) {
            this(robotVersion, System.nanoTime());
        }

        public boolean isExpired() {
            long currentTime = System.nanoTime();
            long expirationTime = TimeUnit.NANOSECONDS.toMillis(timestamp) + EXPIRATION_MILLIS;
            return currentTime > expirationTime;
        }
    }
}
