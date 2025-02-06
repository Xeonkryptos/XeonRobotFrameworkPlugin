package com.github.jnhyperion.hyperrobotframeworkplugin.ide;

import com.github.jnhyperion.hyperrobotframeworkplugin.MyLogger;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PostStartupActivity implements Disposable, ProjectActivity {

    @Override
    public void dispose() {
        MyLogger.logger.debug("dispose");
    }

    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        MyLogger.logger.debug("runActivity: " + project.getName());
        RobotListenerMgr.getInstance().initializeListeners(project);
        return null;
    }
}
