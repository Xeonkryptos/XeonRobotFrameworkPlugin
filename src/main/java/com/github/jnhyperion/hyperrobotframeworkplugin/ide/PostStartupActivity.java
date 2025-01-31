package com.github.jnhyperion.hyperrobotframeworkplugin.ide;

import com.github.jnhyperion.hyperrobotframeworkplugin.MyLogger;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity.DumbAware;
import org.jetbrains.annotations.NotNull;

public class PostStartupActivity implements Disposable, DumbAware {

    @Override
    public void dispose() {
        MyLogger.logger.debug("dispose");
    }

    @Override
    public void runActivity(@NotNull Project project) {
        MyLogger.logger.debug("runActivity: " + project.getName());
        RobotListenerMgr.getInstance().initializeListeners(project);
    }
}
