package dev.xeonkryptos.xeonrobotframeworkplugin.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.Service.Level;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.List;

@Service(Level.PROJECT)
@State(name = "RobotOptionsProvider", storages = { @Storage(value = "$WORKSPACE_FILE$", roamingType = RoamingType.DISABLED) })
public final class RobotOptionsProvider implements PersistentStateComponent<RobotOptionsProvider.State> {

    private final State state = new State();

    private Collator parameterNameCollator;

    public static RobotOptionsProvider getInstance(Project project) {
        return project.getService(RobotOptionsProvider.class);
    }

    public boolean capitalizeKeywords() {
        return state.capitalizeKeywords;
    }

    public void setCapitalizeKeywords(boolean capitalizeKeywords) {
        state.capitalizeKeywords = capitalizeKeywords;
    }

    public boolean multilineIndentation() {
        return state.multilineIndentation;
    }

    public void setMultilineIndentation(boolean multilineIndentation) {
        state.multilineIndentation = multilineIndentation;
    }

    public Collator getParameterNameCollator() {
        if (parameterNameCollator == null) {
            updateParameterNameCollator();
        }
        return parameterNameCollator;
    }

    public String parameterNameCollationRules() {
        return state.parameterNameCollationRules;
    }

    public void setParameterNameCollationRules(String parameterNameCollationRules) {
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

    public String canParseParameterNameCollationRules(String parameterNameCollationRules) {
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

    public boolean pythonLiveInspection() {
        return state.pythonLiveInspection;
    }

    public void setPythonLiveInspection(boolean pythonLiveInspection) {
        state.pythonLiveInspection = pythonLiveInspection;
    }

    public List<String> getPythonLiveInspectionDecorators() {
        return state.pythonLiveInspectionDecorators;
    }

    public void setPythonLiveInspectionDecorators(List<String> pythonLiveInspectionDecorators) {
        state.pythonLiveInspectionDecorators = pythonLiveInspectionDecorators;
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state.capitalizeKeywords = state.capitalizeKeywords;
        this.state.smartAutoEncloseVariable = state.smartAutoEncloseVariable;
        this.state.multilineIndentation = state.multilineIndentation;
        this.state.parameterNameCollationRules = state.parameterNameCollationRules;
        this.state.pythonLiveInspection = state.pythonLiveInspection;
        this.state.pythonLiveInspectionDecorators = state.pythonLiveInspectionDecorators;
    }

    public static class State {
        public boolean capitalizeKeywords = true;
        public boolean smartAutoEncloseVariable = true;
        public boolean multilineIndentation = true;
        public String parameterNameCollationRules = "& A < Ä = Ae & a < ä = ae & O < Ö = Oe & o < ö = oe & U < Ü = Ue & u < ü = ue & S < ß = Ss & s < ß = ss";
        public boolean pythonLiveInspection = false;
        public List<String> pythonLiveInspectionDecorators = List.of();
    }
}
