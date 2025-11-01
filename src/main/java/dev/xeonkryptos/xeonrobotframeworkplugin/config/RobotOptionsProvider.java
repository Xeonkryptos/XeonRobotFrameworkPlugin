package dev.xeonkryptos.xeonrobotframeworkplugin.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.List;

@State(name = "RobotOptionsProvider", storages = { @Storage("$WORKSPACE_FILE$") })
public class RobotOptionsProvider implements PersistentStateComponent<RobotOptionsProvider.State> {

    private final State state = new State();

    private Collator parameterNameCollator;

    public static RobotOptionsProvider getInstance(Project project) {
        return project.getService(RobotOptionsProvider.class);
    }

    public final boolean capitalizeKeywords() {
        return state.capitalizeKeywords;
    }

    public final void setCapitalizeKeywords(boolean capitalizeKeywords) {
        state.capitalizeKeywords = capitalizeKeywords;
    }

    public final boolean smartAutoEncloseVariable() {
        return state.smartAutoEncloseVariable;
    }

    public final void setSmartAutoEncloseVariable(boolean smartAutoEncloseVariable) {
        state.smartAutoEncloseVariable = smartAutoEncloseVariable;
    }

    public final boolean multilineIndentation() {
        return state.multilineIndentation;
    }

    public final void setMultilineIndentation(boolean multilineIndentation) {
        state.multilineIndentation = multilineIndentation;
    }

    public final boolean testsOnlyMode() {
        return state.testsOnlyMode;
    }

    public final void setTestsOnlyMode(boolean testsOnlyMode) {
        state.testsOnlyMode = testsOnlyMode;
    }

    public final Collator getParameterNameCollator() {
        if (parameterNameCollator == null) {
            updateParameterNameCollator();
        }
        return parameterNameCollator;
    }

    public final String parameterNameCollationRules() {
        return state.parameterNameCollationRules;
    }

    public final void setParameterNameCollationRules(String parameterNameCollationRules) {
        state.parameterNameCollationRules = parameterNameCollationRules;
        updateParameterNameCollator();
    }

    private void updateParameterNameCollator() {
        RuleBasedCollator baseCollator = (RuleBasedCollator) Collator.getInstance();
        String baseRules = baseCollator.getRules();
        if (state.parameterNameCollationRules != null && !state.parameterNameCollationRules.isBlank()) {
            try {
                parameterNameCollator = new RuleBasedNormalizerCollator(baseRules + state.parameterNameCollationRules);
            } catch (ParseException ignored) {
                parameterNameCollator = getFallbackCollator(baseCollator);
            }
        } else {
            parameterNameCollator = getFallbackCollator(baseCollator);
        }
        parameterNameCollator.setStrength(Collator.TERTIARY);
    }

    private Collator getFallbackCollator(RuleBasedCollator baseCollator) {
        String baseRules = baseCollator.getRules();
        try {
            return new RuleBasedNormalizerCollator(baseRules);
        } catch (ParseException ignored2) {
            return baseCollator;
        }
    }

    public final String canParseParameterNameCollationRules(String parameterNameCollationRules) {
        if (parameterNameCollationRules == null || parameterNameCollationRules.isBlank()) {
            return null;
        }
        try {
            String baseRules = ((RuleBasedCollator) parameterNameCollator).getRules();
            new RuleBasedCollator(baseRules + parameterNameCollationRules);
        } catch (ParseException e) {
            return e.getMessage();
        }
        return null;
    }

    public final boolean pythonLiveInspection() {
        return state.pythonLiveInspection;
    }

    public final void setPythonLiveInspection(boolean pythonLiveInspection) {
        state.pythonLiveInspection = pythonLiveInspection;
    }

    public final String getPythonLiveInspectionAdditionalArguments() {
        return state.pythonLiveInspectionAdditionalArguments;
    }

    public final void setPythonLiveInspectionAdditionalArguments(String pythonLiveInspectionAdditionalArguments) {
        state.pythonLiveInspectionAdditionalArguments = pythonLiveInspectionAdditionalArguments;
    }

    public final List<String> getPythonLiveInspectionDecorators() {
        return state.pythonLiveInspectionDecorators;
    }

    public final void setPythonLiveInspectionDecorators(List<String> pythonLiveInspectionDecorators) {
        state.pythonLiveInspectionDecorators = pythonLiveInspectionDecorators;
    }

    @Override
    public @Nullable State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state.capitalizeKeywords = state.capitalizeKeywords;
        this.state.smartAutoEncloseVariable = state.smartAutoEncloseVariable;
        this.state.multilineIndentation = state.multilineIndentation;
        this.state.testsOnlyMode = state.testsOnlyMode;
        this.state.parameterNameCollationRules = state.parameterNameCollationRules;
        this.state.pythonLiveInspection = state.pythonLiveInspection;
        this.state.pythonLiveInspectionAdditionalArguments = state.pythonLiveInspectionAdditionalArguments;
        this.state.pythonLiveInspectionDecorators = state.pythonLiveInspectionDecorators;
    }

    public static class State {
        public boolean capitalizeKeywords = true;
        public boolean smartAutoEncloseVariable = true;
        public boolean multilineIndentation = true;
        public boolean testsOnlyMode = true;
        public String parameterNameCollationRules = "& A < Ä = Ae & a < ä = ae & O < Ö = Oe & o < ö = oe & U < Ü = Ue & u < ü = ue & S < ß = Ss & s < ß = ss";
        public boolean pythonLiveInspection = false;
        public String pythonLiveInspectionAdditionalArguments = "-m robot.libdoc .robotframework-ls";
        public List<String> pythonLiveInspectionDecorators = List.of();
    }
}
