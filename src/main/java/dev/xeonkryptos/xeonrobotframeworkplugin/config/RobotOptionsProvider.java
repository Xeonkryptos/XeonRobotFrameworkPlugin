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
import java.util.Collections;
import java.util.List;

@Service(Level.PROJECT)
@State(name = "RobotOptionsProvider", storages = { @Storage(value = "$WORKSPACE_FILE$", roamingType = RoamingType.DISABLED) })
public final class RobotOptionsProvider implements PersistentStateComponent<RobotOptionsProvider.State> {

    private final State state = new State();

    private List<String> enabledDefaultLanguageCodes;
    private List<String> enabledCustomLanguageFiles;

    private Collator parameterNameCollator;

    public static RobotOptionsProvider getInstance(Project project) {
        return project.getService(RobotOptionsProvider.class);
    }

    public List<String> getEnabledDefaultLanguageCodes() {
        return Collections.unmodifiableList(enabledDefaultLanguageCodes);
    }

    public void setEnabledDefaultLanguageCodes(List<String> enabledDefaultLanguageCodes) {
        this.enabledDefaultLanguageCodes = enabledDefaultLanguageCodes;
    }

    public List<String> getEnabledCustomLanguageClasses() {
        return Collections.unmodifiableList(enabledCustomLanguageFiles);
    }

    public void setEnabledCustomLanguageClasses(List<String> enabledCustomLanguageFiles) {
        this.enabledCustomLanguageFiles = enabledCustomLanguageFiles;
    }

    public Collator getParameterNameCollator() {
        if (parameterNameCollator == null) {
            updateParameterNameCollator();
        }
        return parameterNameCollator;
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

    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state.parameterNameCollationRules = state.parameterNameCollationRules;
        this.state.enabledDefaultLanguageCodes = state.enabledDefaultLanguageCodes;
        this.state.enabledCustomLanguageFiles = state.enabledCustomLanguageFiles;
    }

    public static class State {
        public String parameterNameCollationRules = "& A < Ä = Ae & a < ä = ae & O < Ö = Oe & o < ö = oe & U < Ü = Ue & u < ü = ue & S < ß = Ss & s < ß = ss";
        public List<String> enabledDefaultLanguageCodes;
        public List<String> enabledCustomLanguageFiles;
    }
}
