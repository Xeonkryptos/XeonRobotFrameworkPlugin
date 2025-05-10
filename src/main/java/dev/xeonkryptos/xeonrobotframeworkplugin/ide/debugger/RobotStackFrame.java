package dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger;

import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.ColoredTextContainer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.xdebugger.XDebugSession;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.evaluation.XDebuggerEvaluator;
import com.intellij.xdebugger.frame.XCompositeNode;
import com.intellij.xdebugger.frame.XStackFrame;
import com.intellij.xdebugger.frame.XValueChildrenList;
import org.eclipse.lsp4j.debug.Scope;
import org.eclipse.lsp4j.debug.ScopesArguments;
import org.eclipse.lsp4j.debug.ScopesResponse;
import org.eclipse.lsp4j.debug.Source;
import org.eclipse.lsp4j.debug.StackFrame;
import org.eclipse.lsp4j.debug.Variable;
import org.eclipse.lsp4j.debug.VariablesArguments;
import org.eclipse.lsp4j.debug.VariablesResponse;
import org.eclipse.lsp4j.debug.services.IDebugProtocolServer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class RobotStackFrame extends XStackFrame {

    private final StackFrame frame;
    private final IDebugProtocolServer debugServer;
    private final XDebugSession session;

    public RobotStackFrame(StackFrame frame, IDebugProtocolServer debugServer, XDebugSession session) {
        this.frame = frame;
        this.debugServer = debugServer;
        this.session = session;
    }

    @Nullable
    @Override
    public XSourcePosition getSourcePosition() {
        Source source = frame.getSource();
        if (source == null) {
            return null;
        }
        String sourcePath = source.getPath();
        VirtualFile file = VfsUtil.findFile(Path.of(sourcePath), false);
        return XDebuggerUtil.getInstance().createPosition(file, frame.getLine() - 1, frame.getColumn());
    }

    @Nullable
    @Override
    public XDebuggerEvaluator getEvaluator() {
        return new RobotDebuggerEvaluator(debugServer, this);
    }

    @Override
    public void customizePresentation(@NotNull ColoredTextContainer component) {
        if (frame.getSource() == null) {
            String name = frame.getName();
            if (name == null) {
                name = "";
            }
            component.append(name, SimpleTextAttributes.REGULAR_ATTRIBUTES);
        } else {
            super.customizePresentation(component);
        }
    }

    @Override
    public void computeChildren(@NotNull XCompositeNode node) {
        ScopesArguments scopesArguments = new ScopesArguments();
        scopesArguments.setFrameId(frame.getId());

        try {
            ScopesResponse scopesResponse = debugServer.scopes(scopesArguments).get();
            XValueChildrenList list = new XValueChildrenList();
            Optional<Scope> localScopeOpt = Arrays.stream(scopesResponse.getScopes()).filter(scope -> "local".equalsIgnoreCase(scope.getName())).findFirst();
            if (localScopeOpt.isEmpty()) {
                return;
            }
            Scope localScope = localScopeOpt.get();
            VariablesArguments variablesArguments = new VariablesArguments();
            variablesArguments.setVariablesReference(localScope.getVariablesReference());
            VariablesResponse localVariables = debugServer.variables(variablesArguments).get();
            for (Variable variable : localVariables.getVariables()) {
                list.add(variable.getName(), new RobotNamedValue(variable, debugServer, session));
            }

            for (Scope x : scopesResponse.getScopes()) {
                if (!"local".equalsIgnoreCase(x.getName())) {
                    int variableRef = x.getVariablesReference();
                    String groupName = x.getName();
                    variablesArguments = new VariablesArguments();
                    variablesArguments.setVariablesReference(variableRef);
                    VariablesResponse variables = debugServer.variables(variablesArguments).get();
                    RobotValueGroup group = new RobotValueGroup(groupName, variables, variableRef, debugServer, session);
                    list.addBottomGroup(group);
                }
            }

            node.addChildren(list, true);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public StackFrame getFrame() {
        return frame;
    }

    public XDebugSession getSession() {
        return session;
    }
}
