package dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger;

import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import org.eclipse.lsp4j.debug.EvaluateArguments;
import org.eclipse.lsp4j.debug.EvaluateResponse;
import org.eclipse.lsp4j.debug.Variable;
import org.eclipse.lsp4j.debug.services.IDebugProtocolServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ExecutionException;

public class RobotDebuggerEvaluator extends XDebuggerEvaluator {

    private final IDebugProtocolServer debugServer;
    private final RobotStackFrame frame;

    public RobotDebuggerEvaluator(IDebugProtocolServer debugServer, RobotStackFrame frame) {
        this.debugServer = debugServer;
        this.frame = frame;
    }

    @Override
    public void evaluate(@NotNull String expression, @NotNull XEvaluationCallback callback, @Nullable XSourcePosition expressionPosition) {
        EvaluateArguments evaluateArguments = new EvaluateArguments();
        evaluateArguments.setExpression(expression);
        evaluateArguments.setFrameId(frame.getFrame().getId());
        try {
            EvaluateResponse result = debugServer.evaluate(evaluateArguments).get();
            Variable variable = new Variable();
            variable.setValue(result.getResult());
            variable.setEvaluateName(expression);
            variable.setVariablesReference(result.getVariablesReference());
            variable.setType(result.getType());
            variable.setPresentationHint(result.getPresentationHint());
            callback.evaluated(new RobotNamedValue(variable, debugServer, frame.getSession()));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
