package com.github.jnhyperion.hyperrobotframeworkplugin.ide.debugger;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution.RobotRunConfiguration;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.xdebugger.XDebugSession;
import com.jetbrains.python.debugger.PyDebugRunner;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class RobotDebugRunner extends PyDebugRunner {

    @NonNls
    @NotNull
    @Override
    public String getRunnerId() {
        return "HyperRobotFrameworkDebugRunner";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        if (super.canRun(executorId, profile) && profile instanceof RobotRunConfiguration) {
            return DefaultDebugExecutor.EXECUTOR_ID.equals(executorId);
        }
        return false;
    }

    @Override
    protected void initSession(XDebugSession session, RunProfileState state, Executor executor) {
        // TODO: Start second debugger process for Robot Framework and register it as a child of the main debugger process
        //  The normal debugger for python is started when reaching this point
        super.initSession(session, state, executor);
    }
}
