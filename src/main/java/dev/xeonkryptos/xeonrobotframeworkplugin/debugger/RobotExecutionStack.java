package dev.xeonkryptos.xeonrobotframeworkplugin.debugger;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.frame.XExecutionStack;
import com.intellij.xdebugger.frame.XStackFrame;
import org.eclipse.lsp4j.debug.StackFrame;
import org.eclipse.lsp4j.debug.StackTraceResponse;
import org.eclipse.lsp4j.debug.services.IDebugProtocolServer;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("DialogTitleCapitalization")
public class RobotExecutionStack extends XExecutionStack {

    private final StackTraceResponse stack;
    private final IDebugProtocolServer debugServer;
    private final XDebugSession session;

    public RobotExecutionStack(StackTraceResponse stack, IDebugProtocolServer debugServer, XDebugSession session) {
        super("Robot Framework Execution Stack");

        this.stack = stack;
        this.debugServer = debugServer;
        this.session = session;
    }

    @Nullable
    @Override
    public XStackFrame getTopFrame() {
        StackFrame[] stackFrames = stack.getStackFrames();
        if (stackFrames == null) {
            return null;
        }
        StackFrame stackFrame = stackFrames[0];
        return new RobotStackFrame(stackFrame, debugServer, session);
    }

    @Override
    public void computeStackFrames(int firstFrameIndex, XStackFrameContainer container) {
        if (container != null) {
            StackFrame[] stackFrames = stack.getStackFrames();
            List<RobotStackFrame> robotFrames = Arrays.stream(stackFrames)
                                                      .skip(firstFrameIndex)
                                                      .map(stackFrame -> new RobotStackFrame(stackFrame, debugServer, session))
                                                      .toList();
            container.addStackFrames(robotFrames, true);
        }
    }
}
