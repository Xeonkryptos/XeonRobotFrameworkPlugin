package dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger;

import dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger.breakpoint.RobotExceptionBreakpointHandler;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger.breakpoint.RobotExceptionBreakpointProperties;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger.breakpoint.RobotLineBreakpointHandler;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger.breakpoint.RobotLineBreakpointProperties;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger.dap.RobotDebugAdapterProtocolCommunicator;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XExpression;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpoint;
import com.intellij.xdebugger.breakpoints.XBreakpointHandler;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

public class RobotDebugProcess {

    private final XDebugSession session;

    private final RobotDebugAdapterProtocolCommunicator robotDAPCommunicator;

    private final List<ExceptionBreakpointInfo> exceptionBreakpoints = new ArrayList<>();

    private final List<BreakPointInfo> breakpoints = new CopyOnWriteArrayList<>();
    private final Map<VirtualFile, Map<Integer, BreakPointInfo>> breakpointMap = new LinkedHashMap<>();
    private final ReentrantLock breakpointsMapMutex = new ReentrantLock();

    private final RobotLineBreakpointHandler breakpointHandler = new RobotLineBreakpointHandler(this);
    private final RobotExceptionBreakpointHandler exceptionBreakpointHandler = new RobotExceptionBreakpointHandler(this);

    private OneTimeBreakpointInfo _oneTimeBreakpointInfo;

    public RobotDebugProcess(@NotNull XDebugSession session, RobotDebugAdapterProtocolCommunicator robotDAPCommunicator) {
        this.session = session;
        this.robotDAPCommunicator = robotDAPCommunicator;

        robotDAPCommunicator.getAfterInitialize().advise(Lifetime.Companion.getEternal(), ignore -> {
            ApplicationManager.getApplication().executeOnPooledThread(() -> sendBreakpointRequest());
            return null;
        });
        robotDAPCommunicator.getDebugClient().getOnStopped().advise(Lifetime.Companion.getEternal(), args -> {
            ApplicationManager.getApplication().executeOnPooledThread(() -> handleOnStopped(args));
            return null;
        });
    }

    private RobotSuspendContext createRobotCodeSuspendContext(int threadId) throws ExecutionException, InterruptedException, TimeoutException {
        IDebugProtocolServer debugServer = robotDAPCommunicator.getDebugServer();
        StackTraceArguments stackTraceArguments = new StackTraceArguments();
        stackTraceArguments.setThreadId(threadId);
        StackTraceResponse stackTraceResponse = debugServer.stackTrace(stackTraceArguments).get(5L, TimeUnit.SECONDS);
        return new RobotSuspendContext(stackTraceResponse, threadId, debugServer, session);
    }

    private void handleOnStopped(StoppedEventArguments args) {
        try {
            final RobotSuspendContext robotCodeSuspendContext = createRobotCodeSuspendContext(args.getThreadId());
            if (session.areBreakpointsMuted()) {
                ContinueArguments continueArguments = new ContinueArguments();
                continueArguments.setThreadId(args.getThreadId());
                robotDAPCommunicator.getDebugServer().continue_(continueArguments).get();
                return;
            }
            switch (args.getReason()) {
                case "breakpoint" -> {
                    BreakPointInfo bp = breakpoints.stream()
                                                   .filter(breakPointInfo -> breakPointInfo.id != null)
                                                   .filter(breakPointInfo -> ArrayUtil.contains(breakPointInfo.id, args.getHitBreakpointIds()))
                                                   .findFirst()
                                                   .orElse(null);

                    if (bp instanceof LineBreakpointInfo lineBreakpointInfo) {
                        if (!session.breakpointReached(lineBreakpointInfo.breakpoint, null, robotCodeSuspendContext)) {
                            ContinueArguments continueArguments = new ContinueArguments();
                            continueArguments.setThreadId(args.getThreadId());
                            robotDAPCommunicator.getDebugServer().continue_(continueArguments).get();
                        }
                    } else {
                        session.positionReached(robotCodeSuspendContext);
                    }
                }
                case "exception" -> {
                    breakpointsMapMutex.lock();
                    ExceptionBreakpointInfo exceptionBreakpointInfo = null;
                    try {
                        if (!exceptionBreakpoints.isEmpty()) {
                            exceptionBreakpointInfo = exceptionBreakpoints.getFirst();
                        }
                    } finally {
                        breakpointsMapMutex.unlock();
                    }
                    if (exceptionBreakpointInfo == null || !session.breakpointReached(exceptionBreakpointInfo.breakpoint, null, robotCodeSuspendContext)) {
                        ContinueArguments continueArguments = new ContinueArguments();
                        continueArguments.setThreadId(args.getThreadId());
                        robotDAPCommunicator.getDebugServer().continue_(continueArguments).get();
                    }
                }
                default -> session.positionReached(robotCodeSuspendContext);
            }
            removeCurrentOneTimeBreakpoint();
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
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
                LineBreakpointInfo lineBreakpointInfo = new LineBreakpointInfo(null, breakpoint);
                breakpoints.add(lineBreakpointInfo);

                Map<Integer, BreakPointInfo> bpMap = breakpointMap.computeIfAbsent(sourcePosition.getFile(), file -> new LinkedHashMap<>());
                bpMap.put(breakpoint.getLine(), lineBreakpointInfo);

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
                    breakpoints.removeIf(bpInfo -> bpInfo instanceof LineBreakpointInfo lineBreakpointInfo && lineBreakpointInfo.breakpoint.equals(breakpoint));

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

        Collection<BreakPointInfo> breakpoints = breakpointMap.get(file).values();
        if (breakpoints.isEmpty()) {
            return;
        }
        SetBreakpointsArguments arguments = new SetBreakpointsArguments();
        Source source = new Source();
        source.setPath(file.toNioPath().toString());
        arguments.setSource(source);

        SourceBreakpoint[] dapBreakpoints = breakpoints.stream().map(bp -> {
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
                                                      .filter(x -> x.getLine() - 1 == breakpoint.line)
                                                      .findFirst()
                                                      .orElse(null);
                if (responseBreakpoint != null) {
                    breakpoint.id = responseBreakpoint.getId();

                    LineBreakpointInfo lineBreakpointInfo = (LineBreakpointInfo) breakpoint;
                    if (responseBreakpoint.isVerified()) {
                        session.setBreakpointVerified(lineBreakpointInfo.breakpoint);
                    } else {
                        session.setBreakpointInvalid(lineBreakpointInfo.breakpoint, "Invalid breakpoint");
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
        bpMap.put(position.getLine(), _oneTimeBreakpointInfo);

        sendBreakpointRequest(position.getFile());
        resume(context);
    }

    public void startPausing() {
        PauseArguments pauseArguments = new PauseArguments();
        pauseArguments.setThreadId(0);
        try {
            robotDAPCommunicator.getDebugServer().pause(pauseArguments).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

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

    public void stop() {
        try {
            TerminateArguments terminateArguments = new TerminateArguments();
            terminateArguments.setRestart(false);
            robotDAPCommunicator.getDebugServer().terminate(terminateArguments).get();
        } catch (Exception ignore) {
            // Ignore may be the server is already terminated
        }
    }

    public XBreakpointHandler<?> @NotNull [] getBreakpointHandlers() {
        return new XBreakpointHandler[] { breakpointHandler, exceptionBreakpointHandler };
    }

    private static class BreakPointInfo {

        protected volatile Integer id;
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
