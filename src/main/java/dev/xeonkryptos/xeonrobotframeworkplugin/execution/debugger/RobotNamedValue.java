package dev.xeonkryptos.xeonrobotframeworkplugin.execution.debugger;

import com.intellij.icons.AllIcons;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XNamedValue;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueNode;
import com.intellij.xdebugger.frame.XValuePlace;
import org.eclipse.lsp4j.debug.Variable;
import org.eclipse.lsp4j.debug.VariablesArguments;
import org.eclipse.lsp4j.debug.VariablesResponse;
import org.eclipse.lsp4j.debug.services.IDebugProtocolServer;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

public class RobotNamedValue extends XNamedValue {

    private final Variable variable;
    private final IDebugProtocolServer debugServer;
    private final XDebugSession session;

    public RobotNamedValue(Variable variable, IDebugProtocolServer debugServer, XDebugSession session) {
        super(variable.getName() != null ? variable.getName() : "");
        this.variable = variable;
        this.debugServer = debugServer;
        this.session = session;
    }

    @Override
    public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace place) {
        node.setPresentation(AllIcons.Nodes.Variable, variable.getType(), variable.getValue(), variable.getVariablesReference() != 0);
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        if (variable.getVariablesReference() != 0) {
            XValueChildrenList list = new XValueChildrenList();
            VariablesArguments variablesArguments = new VariablesArguments();
            variablesArguments.setVariablesReference(variable.getVariablesReference());
            try {
                VariablesResponse variablesResponse = debugServer.variables(variablesArguments).get();
                for (Variable receivedVariable : variablesResponse.getVariables()) {
                    list.add(receivedVariable.getName(), new RobotNamedValue(receivedVariable, debugServer, session));
                }
                node.addChildren(list, true);
            } catch (InterruptedException | ExecutionException ignored) {}
        }
    }
}
