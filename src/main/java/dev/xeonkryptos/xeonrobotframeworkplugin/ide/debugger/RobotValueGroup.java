package dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger;

import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XValueChildrenList;
import com.intellij.xdebugger.frame.XValueGroup;
import org.eclipse.lsp4j.debug.Variable;
import org.eclipse.lsp4j.debug.VariablesResponse;
import org.eclipse.lsp4j.debug.services.IDebugProtocolServer;
import org.jetbrains.annotations.NotNull;

public class RobotValueGroup extends XValueGroup {

    private final VariablesResponse variables;
    private final IDebugProtocolServer debugServer;
    private final XDebugSession session;

    public RobotValueGroup(String groupName, VariablesResponse variables, IDebugProtocolServer debugServer, XDebugSession session) {
        super(groupName);
        this.variables = variables;
        this.debugServer = debugServer;
        this.session = session;
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        XValueChildrenList list = new XValueChildrenList();
        for (Variable variable : variables.getVariables()) {
            list.add(variable.getName(), new RobotNamedValue(variable, debugServer, session));
        }
        node.addChildren(list, true);
    }
}
