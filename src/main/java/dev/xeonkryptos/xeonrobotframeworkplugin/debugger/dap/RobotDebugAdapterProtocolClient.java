package dev.xeonkryptos.xeonrobotframeworkplugin.debugger.dap;

import dev.xeonkryptos.xeonrobotframeworkplugin.debugger.dap.model.RobotEnqueuedArguments;
import dev.xeonkryptos.xeonrobotframeworkplugin.debugger.dap.model.RobotExecutionEventArguments;
import dev.xeonkryptos.xeonrobotframeworkplugin.debugger.dap.model.RobotExitedEventArguments;
import dev.xeonkryptos.xeonrobotframeworkplugin.debugger.dap.model.RobotLogMessageEventArguments;
import com.jetbrains.rd.util.reactive.Signal;
import org.eclipse.lsp4j.debug.ExitedEventArguments;
import org.eclipse.lsp4j.debug.OutputEventArguments;
import org.eclipse.lsp4j.debug.StoppedEventArguments;
import org.eclipse.lsp4j.debug.TerminatedEventArguments;
import org.eclipse.lsp4j.debug.services.IDebugProtocolClient;
import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;

public class RobotDebugAdapterProtocolClient implements IDebugProtocolClient {

    private final Signal<TerminatedEventArguments> onTerminated = new Signal<>();
    private final Signal<StoppedEventArguments> onStopped = new Signal<>();
    private final Signal<ExitedEventArguments> onExited = new Signal<>();
    private final Signal<RobotEnqueuedArguments> onRobotEnqueued = new Signal<>();
    private final Signal<RobotExecutionEventArguments> onRobotStarted = new Signal<>();
    private final Signal<RobotExecutionEventArguments> onRobotEnded = new Signal<>();
    private final Signal<RobotExecutionEventArguments> onRobotSetFailed = new Signal<>();
    private final Signal<RobotExitedEventArguments> onRobotExited = new Signal<>();
    private final Signal<RobotLogMessageEventArguments> onRobotLog = new Signal<>();
    private final Signal<RobotLogMessageEventArguments> onRobotMessage = new Signal<>();
    private final Signal<OutputEventArguments> onOutput = new Signal<>();

    @Override
    public void exited(ExitedEventArguments args) {
        onExited.fire(args);
    }

    @Override
    public void terminated(TerminatedEventArguments args) {
        onTerminated.fire(args);
    }

    @Override
    public void stopped(StoppedEventArguments args) {
        onStopped.fire(args);
    }

    @JsonNotification("robotEnqueued")
    public void robotEnqueued(RobotEnqueuedArguments args) {
        onRobotEnqueued.fire(args);
    }

    @JsonNotification("robotStarted")
    public void robotStarted(RobotExecutionEventArguments args) {
        onRobotStarted.fire(args);
    }

    @JsonNotification("robotEnded")
    public void robotEnded(RobotExecutionEventArguments args) {
        onRobotEnded.fire(args);
    }

    @JsonNotification("robotSetFailed")
    public void robotSetFailed(RobotExecutionEventArguments args) {
        onRobotSetFailed.fire(args);
    }

    @JsonNotification("robotExited")
    public void robotExited(RobotExitedEventArguments args) {
        onRobotExited.fire(args);
    }

    @JsonNotification("robotLog")
    public void robotLog(RobotLogMessageEventArguments args) {
        onRobotLog.fire(args);
    }

    @JsonNotification("robotMessage")
    public void robotMessage(RobotLogMessageEventArguments args) {
        onRobotMessage.fire(args);
    }

    @Override
    public void output(OutputEventArguments args) {
        onOutput.fire(args);
    }

    public Signal<TerminatedEventArguments> getOnTerminated() {
        return onTerminated;
    }

    public Signal<StoppedEventArguments> getOnStopped() {
        return onStopped;
    }

    public Signal<ExitedEventArguments> getOnExited() {
        return onExited;
    }

    public Signal<RobotEnqueuedArguments> getOnRobotEnqueued() {
        return onRobotEnqueued;
    }

    public Signal<RobotExecutionEventArguments> getOnRobotStarted() {
        return onRobotStarted;
    }

    public Signal<RobotExecutionEventArguments> getOnRobotEnded() {
        return onRobotEnded;
    }

    public Signal<RobotExecutionEventArguments> getOnRobotSetFailed() {
        return onRobotSetFailed;
    }

    public Signal<RobotExitedEventArguments> getOnRobotExited() {
        return onRobotExited;
    }

    public Signal<RobotLogMessageEventArguments> getOnRobotLog() {
        return onRobotLog;
    }

    public Signal<RobotLogMessageEventArguments> getOnRobotMessage() {
        return onRobotMessage;
    }

    public Signal<OutputEventArguments> getOnOutput() {
        return onOutput;
    }
}
