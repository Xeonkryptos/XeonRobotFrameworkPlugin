package com.github.jnhyperion.hyperrobotframeworkplugin.ide.execution;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunnerSettings;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ProgramRunner;
import com.intellij.openapi.wm.ToolWindowId;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * This class is responsible for forwarding the run to the proper class
 * as well as the related debug session in the intellij side.
 * <p>
 * Note that the flow is the same in run or debug mode (the only difference
 * is that that the `noDebug` flag passed to the debug adapter is different).
 */
public class RobotProgramRunner implements ProgramRunner<RunnerSettings> {

    @NotNull
    @NonNls
    @Override
    public String getRunnerId() {
        return "RobotFramework";
    }

    @Override
    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        if (profile instanceof RobotRunConfiguration) {
            if (ToolWindowId.RUN.equals(executorId)) {
                return true;
            } else {
                return ToolWindowId.DEBUG.equals(executorId);
            }
        }
        return false;
    }

    @Override
    public void execute(@NotNull ExecutionEnvironment env) throws ExecutionException {
//        final RobotRunProfileStateRobotDAPStarter state = (RobotRunProfileStateRobotDAPStarter) env.getState();
//        ExecutionManager executionManager = ExecutionManager.getInstance(env.getProject());
//
//        FileDocumentManager.getInstance().saveAllDocuments();
//        Executor executor = env.getExecutor();
//
//        executionManager.startRunProfile(env, () -> {
//            AsyncPromise<RunContentDescriptor> promise = new AsyncPromise<>();
//
//            ExecutionResult executionResult;
//            try {
//                executionResult = state.execute(executor, this);
//                // At this point the debug adapter is running (but still not executing any target code
//                // as we still didn't do the launch/configurationDone).
//                if (executionResult == null) {
//                    promise.setResult(null);
//                    return promise;
//                }
//
//                final XDebuggerManager debuggerManager = XDebuggerManager.getInstance(env.getProject());
//                final XDebugSession debugSession = debuggerManager.startSession(env, new XDebugProcessStarter() {
//                    @Override
//                    @NotNull
//                    public XDebugProcess start(@NotNull final XDebugSession session) throws ExecutionException {
//                        try {
//                            return new RobotDebugProcess(executor, session, executionResult.getProcessHandler());
//                        } catch (Exception e) {
//                            throw new ExecutionException(e);
//                        }
//                    }
//                });
//                promise.setResult(debugSession.getRunContentDescriptor());
//                return promise;
//            } catch (ExecutionException e) {
//                promise.setError(e);
//                return promise;
//            }
//        });
    }
}
