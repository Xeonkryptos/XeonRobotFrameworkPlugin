package com.github.jnhyperion.hyperrobotframeworkplugin.ide.debugger;

import com.intellij.openapi.util.Pair;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XSuspendContext;
import org.eclipse.lsp4j.debug.StackTraceResponse;
import org.eclipse.lsp4j.debug.Variable;
import org.eclipse.lsp4j.debug.services.IDebugProtocolServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class RobotSuspendContext extends XSuspendContext {

    private final StackTraceResponse stack;
    private final int threadId;
    private final IDebugProtocolServer debugServer;
    private final XDebugSession session;

    private final Map<Pair<Integer, String>, Variable> variablesCache = new LinkedHashMap<>();

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

    public Variable getCachedVariableOrDefault(Pair<Integer, String> key, Variable defaultValue) {
        return variablesCache.getOrDefault(key, defaultValue);
    }

    public int getThreadId() {
        return threadId;
    }
}
