package com.github.jnhyperion.hyperrobotframeworkplugin.ide.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public final boolean allowGlobalVariables() {
        return this.state.globalVariables;
    }

    public final void setGlobalVariables(boolean globalVariables) {
        this.state.globalVariables = globalVariables;
    }

    public final boolean capitalizeKeywords() {
        return this.state.capitalizeKeywords;
    }

    public final void setCapitalizeKeywords(boolean capitalizeKeywords) {
        this.state.capitalizeKeywords = capitalizeKeywords;
    }

    public final boolean inlineVariableSearch() {
        return this.state.inlineVariableSearch;
    }

    public final void setInlineVariableSearch(boolean inlineVariableSearch) {
        this.state.inlineVariableSearch = inlineVariableSearch;
    }

    public final boolean reformatOnSave() {
        return this.state.reformatOnSave;
    }

    public final void setReformatOnSave(boolean reformatOnSave) {
        this.state.reformatOnSave = reformatOnSave;
    }

    public final boolean smartAutoEncloseVariable() {
        return this.state.smartAutoEncloseVariable;
    }

    public final void setSmartAutoEncloseVariable(boolean smartAutoEncloseVariable) {
        this.state.smartAutoEncloseVariable = smartAutoEncloseVariable;
    }

    @Override
    public @Nullable State getState() {
        return this.state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state.debug = state.debug;
        this.state.transitiveImports = state.transitiveImports;
        this.state.globalVariables = state.globalVariables;
        this.state.capitalizeKeywords = state.capitalizeKeywords;
        this.state.inlineVariableSearch = state.inlineVariableSearch;
        this.state.reformatOnSave = state.reformatOnSave;
        this.state.smartAutoEncloseVariable = state.smartAutoEncloseVariable;
    }

    public static class State {
        public boolean transitiveImports = true;
        public boolean globalVariables = true;
        public boolean debug = false;
        public boolean capitalizeKeywords = true;
        public boolean inlineVariableSearch = false;
        public boolean reformatOnSave = true;
        public boolean smartAutoEncloseVariable = true;
    }
}
