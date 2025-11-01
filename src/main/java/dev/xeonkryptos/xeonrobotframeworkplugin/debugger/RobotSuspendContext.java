package dev.xeonkryptos.xeonrobotframeworkplugin.debugger;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XSuspendContext;
import org.eclipse.lsp4j.debug.StackTraceResponse;
import org.eclipse.lsp4j.debug.services.IDebugProtocolServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotSuspendContext extends XSuspendContext {

    private final StackTraceResponse stack;
    private final int threadId;
    private final IDebugProtocolServer debugServer;
    private final XDebugSession session;

    public RobotSuspendContext(StackTraceResponse stack, int threadId, IDebugProtocolServer debugServer, XDebugSession session) {
        this.stack = stack;
        this.threadId = threadId;
        this.debugServer = debugServer;
        this.session = session;
    }

    @Override
    public XExecutionStack @NotNull [] getExecutionStacks() {
        return new RobotExecutionStack[] { new RobotExecutionStack(stack, debugServer, session) };
    }

    @Nullable
    @Override
    public XExecutionStack getActiveExecutionStack() {
        return new RobotExecutionStack(stack, debugServer, session);
    }

    public int getThreadId() {
        return threadId;
    }
}
