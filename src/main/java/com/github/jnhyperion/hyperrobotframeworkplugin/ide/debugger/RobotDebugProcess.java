package com.github.jnhyperion.hyperrobotframeworkplugin.ide.debugger;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.debugger.dap.RobotDebugAdapterProtocolCommunicator;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XExpression;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import com.intellij.xdebugger.frame.XSuspendContext;
import com.jetbrains.rd.util.lifetime.Lifetime;
import org.eclipse.lsp4j.debug.Breakpoint;
import org.eclipse.lsp4j.debug.ContinueArguments;
import org.eclipse.lsp4j.debug.NextArguments;
import org.eclipse.lsp4j.debug.PauseArguments;
import org.eclipse.lsp4j.debug.SetBreakpointsArguments;
import org.eclipse.lsp4j.debug.SetBreakpointsResponse;
import org.eclipse.lsp4j.debug.Source;
import org.eclipse.lsp4j.debug.SourceBreakpoint;
import org.eclipse.lsp4j.debug.StackTraceArguments;
import org.eclipse.lsp4j.debug.StackTraceResponse;
import org.eclipse.lsp4j.debug.StepInArguments;
import org.eclipse.lsp4j.debug.StepOutArguments;
import org.eclipse.lsp4j.debug.StoppedEventArguments;
import org.eclipse.lsp4j.debug.TerminateArguments;
import org.eclipse.lsp4j.debug.services.IDebugProtocolServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

public class RobotDebugProcess extends XDebugProcess {

    private final RobotDebuggerEditorsProvider editorsProvider = new RobotDebuggerEditorsProvider();

    private final ExecutionResult executionResult;
    private final RobotDebugAdapterProtocolCommunicator robotDAPCommunicator;

    private final List<ExceptionBreakpointInfo> exceptionBreakpoints = new ArrayList<>();

    private final List<BreakPointInfo> breakpoints = new ArrayList<>();
    private final Map<VirtualFile, Map<Integer, BreakPointInfo>> breakpointMap = new LinkedHashMap<>();
    private final ReentrantLock breakpointsMapMutex = new ReentrantLock();

    private final RobotLineBreakpointHandler breakpointHandler = new RobotLineBreakpointHandler(this);
    private final RobotExceptionBreakpointHandler exceptionBreakpointHandler = new RobotExceptionBreakpointHandler(this);

    private OneTimeBreakpointInfo _oneTimeBreakpointInfo;

    public RobotDebugProcess(@NotNull XDebugSession session, ExecutionResult executionResult, RobotDebugAdapterProtocolCommunicator robotDAPCommunicator) {
        super(session);

        this.executionResult = executionResult;
        this.robotDAPCommunicator = robotDAPCommunicator;

        session.setPauseActionSupported(true);

        robotDAPCommunicator.getAfterInitialize().advise(Lifetime.Companion.getEternal(), ignore -> {
            sendBreakpointRequest();
            return null;
        });
        robotDAPCommunicator.getDebugClient().getOnStopped().advise(Lifetime.Companion.getEternal(), args -> {
            handleOnStopped(args);
            return null;
        });
    }

    private RobotSuspendContext createRobotCodeSuspendContext(int threadId) throws ExecutionException, InterruptedException {
        IDebugProtocolServer debugServer = robotDAPCommunicator.getDebugServer();
        StackTraceArguments stackTraceArguments = new StackTraceArguments();
        stackTraceArguments.setThreadId(threadId);
        StackTraceResponse stackTraceResponse = debugServer.stackTrace(stackTraceArguments).get();
        return new RobotSuspendContext(stackTraceResponse, threadId, debugServer, getSession());
    }

    private void handleOnStopped(StoppedEventArguments args) {
        try {
            switch (args.getReason()) {
                case "breakpoint" -> {
                    BreakPointInfo bp = breakpoints.stream()
                                                   .filter(breakPointInfo -> breakPointInfo.id != null &&
                                                                             ArrayUtil.contains(breakPointInfo.id, args.getHitBreakpointIds()))
                                                   .findFirst()
                                                   .orElse(null);

                    if (bp instanceof LineBreakpointInfo lineBreakpointInfo) {
                        if (!getSession().breakpointReached(lineBreakpointInfo.breakpoint, null, createRobotCodeSuspendContext(args.getThreadId()))) {
                            ContinueArguments continueArguments = new ContinueArguments();
                            continueArguments.setThreadId(args.getThreadId());
                            robotDAPCommunicator.getDebugServer().continue_(continueArguments).get();
                        }
                    } else {
                        getSession().positionReached(createRobotCodeSuspendContext(args.getThreadId()));
                    }
                }

                case "exception" -> {
                    if (!getSession().breakpointReached(exceptionBreakpoints.get(0).breakpoint, null, createRobotCodeSuspendContext(args.getThreadId()))) {
                        ContinueArguments continueArguments = new ContinueArguments();
                        continueArguments.setThreadId(args.getThreadId());
                        robotDAPCommunicator.getDebugServer().continue_(continueArguments).get();
                    }
                }

                default -> getSession().positionReached(createRobotCodeSuspendContext(args.getThreadId()));
            }
            removeCurrentOneTimeBreakpoint();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerExceptionBreakpoint(XBreakpoint<RobotExceptionBreakpointProperties> breakpoint) {
        breakpointsMapMutex.lock();
        try {
            exceptionBreakpoints.add(new ExceptionBreakpointInfo(null, breakpoint));
        } finally {
            breakpointsMapMutex.unlock();
        }
    }

    public void unregisterExceptionBreakpoint(XBreakpoint<RobotExceptionBreakpointProperties> breakpoint) {
        breakpointsMapMutex.lock();
        try {
            exceptionBreakpoints.removeIf(bpInfo -> bpInfo.breakpoint.equals(breakpoint));
        } finally {
            breakpointsMapMutex.unlock();
        }
    }

    public void registerBreakpoint(XLineBreakpoint<RobotLineBreakpointProperties> breakpoint) {
        breakpointsMapMutex.lock();
        try {
            XSourcePosition sourcePosition = breakpoint.getSourcePosition();
            if (sourcePosition != null) {
                if (!breakpointMap.containsKey(sourcePosition.getFile())) {
                    breakpointMap.put(sourcePosition.getFile(), new LinkedHashMap<>());
                }
                Map<Integer, BreakPointInfo> bpMap = breakpointMap.get(sourcePosition.getFile());
                bpMap.put(breakpoint.getLine(), new LineBreakpointInfo(null, breakpoint));
                sendBreakpointRequest(sourcePosition.getFile());
            }
        } finally {
            breakpointsMapMutex.unlock();
        }
    }

    public void unregisterBreakpoint(XLineBreakpoint<RobotLineBreakpointProperties> breakpoint) {
        breakpointsMapMutex.lock();
        try {
            XSourcePosition sourcePosition = breakpoint.getSourcePosition();
            if (sourcePosition != null) {
                if (breakpointMap.containsKey(sourcePosition.getFile())) {
                    Map<Integer, BreakPointInfo> bpMap = breakpointMap.get(sourcePosition.getFile());
                    bpMap.remove(breakpoint.getLine());
                    sendBreakpointRequest(sourcePosition.getFile());
                }
            }
        } finally {
            breakpointsMapMutex.unlock();
        }
    }

    private void sendBreakpointRequest() {
        if (!robotDAPCommunicator.isInitialized()) {
            return;
        }

        for (VirtualFile file : breakpointMap.keySet()) {
            sendBreakpointRequest(file);
        }
    }

    private void sendBreakpointRequest(VirtualFile file) {
        if (!robotDAPCommunicator.isInitialized()) {
            return;
        }

        Set<Map.Entry<Integer, BreakPointInfo>> breakpoints = breakpointMap.get(file).entrySet();
        if (breakpoints.isEmpty()) {
            return;
        }
        SetBreakpointsArguments arguments = new SetBreakpointsArguments();
        Source source = new Source();
        source.setPath(file.toNioPath().toString());
        arguments.setSource(source);

        SourceBreakpoint[] dapBreakpoints = breakpoints.stream().map(entry -> {
            BreakPointInfo bp = entry.getValue();
            SourceBreakpoint sourceBreakpoint = new SourceBreakpoint();
            sourceBreakpoint.setLine(bp.line + 1);
            if (bp instanceof LineBreakpointInfo lineBreakpointInfo) {
                XExpression conditionExpression = lineBreakpointInfo.breakpoint.getConditionExpression();
                XExpression logExpressionObject = lineBreakpointInfo.breakpoint.getLogExpressionObject();

                String condition = conditionExpression != null ? conditionExpression.getExpression() : null;
                String logMessage = logExpressionObject != null ? logExpressionObject.getExpression() : null;
                sourceBreakpoint.setCondition(condition);
                sourceBreakpoint.setLogMessage(logMessage);
            }
            return sourceBreakpoint;
        }).toArray(SourceBreakpoint[]::new);

        arguments.setBreakpoints(dapBreakpoints);

        try {
            SetBreakpointsResponse response = robotDAPCommunicator.getDebugServer().setBreakpoints(arguments).get();

            breakpoints.forEach(breakpoint -> {
                Breakpoint responseBreakpoint = Arrays.stream(response.getBreakpoints())
                                                      .filter(x -> x.getLine() - 1 == breakpoint.getValue().line)
                                                      .findFirst()
                                                      .orElse(null);
                if (responseBreakpoint != null) {
                    breakpoint.getValue().id = responseBreakpoint.getId();

                    LineBreakpointInfo lineBreakpointInfo = (LineBreakpointInfo) breakpoint.getValue();
                    if (lineBreakpointInfo != null) {
                        if (responseBreakpoint.isVerified()) {
                            getSession().setBreakpointVerified(lineBreakpointInfo.breakpoint);
                        } else {
                            getSession().setBreakpointInvalid(lineBreakpointInfo.breakpoint, "Invalid breakpoint");
                        }
                    }
                }
            });
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeCurrentOneTimeBreakpoint() {
        if (_oneTimeBreakpointInfo != null) {
            Map<Integer, BreakPointInfo> bpMap = breakpointMap.get(_oneTimeBreakpointInfo.file);
            if (bpMap != null) {
                bpMap.remove(_oneTimeBreakpointInfo.line);
                sendBreakpointRequest(_oneTimeBreakpointInfo.file);
            }
            _oneTimeBreakpointInfo = null;
        }
    }

    @Override
    public void startStepOver(@Nullable XSuspendContext context) {
        if (context instanceof RobotSuspendContext robotSuspendContext) {
            NextArguments nextArguments = new NextArguments();
            nextArguments.setThreadId(robotSuspendContext.getThreadId());
            try {
                robotDAPCommunicator.getDebugServer().next(nextArguments).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void startStepInto(@Nullable XSuspendContext context) {
        if (context instanceof RobotSuspendContext robotSuspendContext) {
            StepInArguments stepInArguments = new StepInArguments();
            stepInArguments.setThreadId(robotSuspendContext.getThreadId());
            try {
                robotDAPCommunicator.getDebugServer().stepIn(stepInArguments).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void startStepOut(@Nullable XSuspendContext context) {
        if (context instanceof RobotSuspendContext robotSuspendContext) {
            StepOutArguments stepOutArguments = new StepOutArguments();
            stepOutArguments.setThreadId(robotSuspendContext.getThreadId());
            try {
                robotDAPCommunicator.getDebugServer().stepOut(stepOutArguments).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void runToPosition(@NotNull XSourcePosition position, @Nullable XSuspendContext context) {
        if (!breakpointMap.containsKey(position.getFile())) {
            breakpointMap.put(position.getFile(), new LinkedHashMap<>());
        }
        Map<Integer, BreakPointInfo> bpMap = breakpointMap.get(position.getFile());
        removeCurrentOneTimeBreakpoint();
        if (bpMap.containsKey(position.getLine())) {
            return;
        }

        _oneTimeBreakpointInfo = new OneTimeBreakpointInfo(null, position);
        bpMap.put(position.getLine(), new OneTimeBreakpointInfo(null, position));

        sendBreakpointRequest(position.getFile());
        resume(context);
    }

    @Override
    public void startPausing() {
        PauseArguments pauseArguments = new PauseArguments();
        pauseArguments.setThreadId(0);
        try {
            robotDAPCommunicator.getDebugServer().pause(pauseArguments).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resume(@Nullable XSuspendContext context) {
        if (context instanceof RobotSuspendContext robotSuspendContext) {
            ContinueArguments continueArguments = new ContinueArguments();
            continueArguments.setThreadId(robotSuspendContext.getThreadId());
            try {
                robotDAPCommunicator.getDebugServer().continue_(continueArguments).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void stop() {
        try {
            TerminateArguments terminateArguments = new TerminateArguments();
            terminateArguments.setRestart(false);
            robotDAPCommunicator.getDebugServer().terminate(terminateArguments).get();
        } catch (Exception ignore) {
            // Ignore may be the server is already terminated
        }
    }

    @Override
    public XBreakpointHandler<?> @NotNull [] getBreakpointHandlers() {
        return new XBreakpointHandler[] { breakpointHandler, exceptionBreakpointHandler };
    }

    @NotNull
    @Override
    public ExecutionConsole createConsole() {
        return executionResult.getExecutionConsole();
    }

    @NotNull
    @Override
    public XDebuggerEditorsProvider getEditorsProvider() {
        return editorsProvider;
    }

    private static class BreakPointInfo {

        protected Integer id;
        protected final int line;
        protected final VirtualFile file;

        private BreakPointInfo(Integer id, int line, VirtualFile file) {
            this.id = id;
            this.line = line;
            this.file = file;
        }
    }

    private class LineBreakpointInfo extends BreakPointInfo {

        private final XLineBreakpoint<RobotLineBreakpointProperties> breakpoint;

        public LineBreakpointInfo(Integer id, XLineBreakpoint<RobotLineBreakpointProperties> breakpoint) {
            super(id, breakpoint.getLine(), breakpoint.getSourcePosition() != null ? breakpoint.getSourcePosition().getFile() : null);

            this.breakpoint = breakpoint;
        }
    }

    private record ExceptionBreakpointInfo(Integer id, XBreakpoint<RobotExceptionBreakpointProperties> breakpoint) {}

    private class OneTimeBreakpointInfo extends BreakPointInfo {

        private OneTimeBreakpointInfo(Integer id, XSourcePosition position) {
            super(id, position.getLine(), position.getFile());
        }
    }
}
