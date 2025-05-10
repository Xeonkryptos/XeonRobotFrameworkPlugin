package dev.xeonkryptos.xeonrobotframeworkplugin.ide.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.jetbrains.python.psi.PyDecorator;
import com.jetbrains.python.psi.PyDecoratorList;
import com.jetbrains.python.psi.PyFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@State(name = "RobotOptionsProvider", storages = { @Storage("$WORKSPACE_FILE$") })
public class RobotOptionsProvider implements PersistentStateComponent<RobotOptionsProvider.State> {

    private final State state = new State();

    public static RobotOptionsProvider getInstance(Project project) {
        return project.getService(RobotOptionsProvider.class);
    }

    public final boolean isDebug() {
        return this.state.debug;
    }

    public final void setDebug(boolean debug) {
        this.state.debug = debug;
    }

    public final boolean allowTransitiveImports() {
        return this.state.transitiveImports;
    }

    public final void setAllowTransitiveImports(boolean transitiveImports) {
        this.state.transitiveImports = transitiveImports;
    }

    public final boolean capitalizeKeywords() {
        return this.state.capitalizeKeywords;
    }

    public final void setCapitalizeKeywords(boolean capitalizeKeywords) {
        this.state.capitalizeKeywords = capitalizeKeywords;
    }

    public final boolean smartAutoEncloseVariable() {
        return this.state.smartAutoEncloseVariable;
    }

    public final void setSmartAutoEncloseVariable(boolean smartAutoEncloseVariable) {
        this.state.smartAutoEncloseVariable = smartAutoEncloseVariable;
    }

    public final boolean multilineIndentation() {
        return this.state.multilineIndentation;
    }

    public final void setMultilineIndentation(boolean multilineIndentation) {
        this.state.multilineIndentation = multilineIndentation;
    }

    public final boolean pythonLiveInspection() {
        return this.state.pythonLiveInspection;
    }

    public final void setPythonLiveInspection(boolean pythonLiveInspection) {
        this.state.pythonLiveInspection = pythonLiveInspection;
    }

    public final String getPythonLiveInspectionAdditionalArguments() {
        return this.state.pythonLiveInspectionAdditionalArguments;
    }

    public final void setPythonLiveInspectionAdditionalArguments(String pythonLiveInspectionAdditionalArguments) {
        this.state.pythonLiveInspectionAdditionalArguments = pythonLiveInspectionAdditionalArguments;
    }

    public final List<String> getPythonLiveInspectionDecorators() {
        return this.state.pythonLiveInspectionDecorators;
    }

    public final void setPythonLiveInspectionDecorators(List<String> pythonLiveInspectionDecorators) {
        this.state.pythonLiveInspectionDecorators = pythonLiveInspectionDecorators;
    }

    @SuppressWarnings("UnstableApiUsage")
    public final boolean analyzeViaPythonLiveInspection(PyFunction function) {
        if (this.state.pythonLiveInspection) {
            PyDecoratorList decoratorList = function.getDecoratorList();
            if (decoratorList != null) {
                for (PyDecorator decorator : decoratorList.getDecorators()) {
                    String decoratorName = decorator.getName();
                    if (!"keyword".equals(decoratorName) && (this.state.pythonLiveInspectionDecorators.isEmpty()
                                                             || this.state.pythonLiveInspectionDecorators.contains(decoratorName))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable State getState() {
        return this.state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state.debug = state.debug;
        this.state.transitiveImports = state.transitiveImports;
        this.state.capitalizeKeywords = state.capitalizeKeywords;
        this.state.smartAutoEncloseVariable = state.smartAutoEncloseVariable;
        this.state.multilineIndentation = state.multilineIndentation;
        this.state.pythonLiveInspection = state.pythonLiveInspection;
        this.state.pythonLiveInspectionAdditionalArguments = state.pythonLiveInspectionAdditionalArguments;
        this.state.pythonLiveInspectionDecorators = state.pythonLiveInspectionDecorators;
    }

    public static class State {
        public boolean transitiveImports = true;
        public boolean debug = false;
        public boolean capitalizeKeywords = true;
        public boolean smartAutoEncloseVariable = true;
        public boolean multilineIndentation = true;
        public boolean pythonLiveInspection = false;
        public String pythonLiveInspectionAdditionalArguments = "-m robot.libdoc .robotframework-ls";
        public List<String> pythonLiveInspectionDecorators = List.of();
    }
}
