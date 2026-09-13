package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.codeInsight.TailType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.localization.BehaviourDrivenType;
import dev.xeonkryptos.xeonrobotframeworkplugin.localization.GlobalSettingType;
import dev.xeonkryptos.xeonrobotframeworkplugin.localization.LocalSettingType;
import dev.xeonkryptos.xeonrobotframeworkplugin.localization.LocalizationLoadedListener;
import dev.xeonkryptos.xeonrobotframeworkplugin.localization.LocalizationTopics;
import dev.xeonkryptos.xeonrobotframeworkplugin.localization.LocalizationTypeMappingProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.localization.SectionType;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotTailTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

@Service(Service.Level.PROJECT)
public final class RobotKeywordProvider implements Disposable {

    public static final IElementType IMPORT = new RobotElementType("IMPORT");
    public static final IElementType GHERKIN = new RobotElementType("GHERKIN");
    public static final IElementType SYNTAX_MARKER = new RobotElementType("SYNTAX_MARKER");
    public static final IElementType GLOBAL_SETTING_STATEMENT = new RobotElementType("GLOBAL_SETTING_STATEMENT");

    private final RobotKeywordTable keywordTable = new RobotKeywordTable();

    public RobotKeywordProvider(Project project) {
        project.getMessageBus().connect(this).subscribe(LocalizationTopics.LOCALIZATION_LOADED_TOPIC, (LocalizationLoadedListener) this::fillRecommendations);
    }

    public static RobotKeywordProvider getInstance(Project project) {
        return project.getService(RobotKeywordProvider.class);
    }

    private void fillRecommendations(LocalizationTypeMappingProvider typeMappingProvider) {
        keywordTable.clearRecommendations();

        for (SectionType sectionType : SectionType.getEntries()) {
            List<CharSequence> sectionNames = typeMappingProvider.getSectionName(sectionType);
            if (sectionNames != null) {
                sectionNames.forEach(sectionName -> addRecommendation(RobotTypes.SECTION, "*** %s ***".formatted(sectionName), sectionName.toString(), RobotTailTypes.NEW_LINE));
            }
        }

        Set<GlobalSettingType> importSettingTypes = Set.of(GlobalSettingType.LIBRARY, GlobalSettingType.RESOURCE, GlobalSettingType.VARIABLES);
        for (GlobalSettingType globalSettingType : GlobalSettingType.getEntries()) {
            if (!importSettingTypes.contains(globalSettingType)) {
                List<CharSequence> globalSettingNames = typeMappingProvider.getGlobalSettingName(globalSettingType);
                if (globalSettingNames != null) {
                    globalSettingNames.forEach(globalSettingName -> {
                        String settingName = globalSettingName.toString();
                        addRecommendation(GLOBAL_SETTING_STATEMENT, settingName, settingName, RobotTailTypes.TAB);
                    });
                }
            }
        }
        addRecommendation(GLOBAL_SETTING_STATEMENT, RobotNames.FORCE_TAGS_LOCAL_SETTING_NAME, RobotNames.FORCE_TAGS_LOCAL_SETTING_NAME, RobotTailTypes.TAB);
        addRecommendation(GLOBAL_SETTING_STATEMENT, RobotNames.DEFAULT_TAGS_LOCAL_SETTING_NAME, RobotNames.DEFAULT_TAGS_LOCAL_SETTING_NAME, RobotTailTypes.TAB);

        for (LocalSettingType localSettingType : LocalSettingType.getEntries()) {
            List<CharSequence> localSettingNames = typeMappingProvider.getLocalSettingName(localSettingType);
            if (localSettingNames != null) {
                localSettingNames.forEach(localSettingName -> {
                    String settingName = localSettingName.toString();
                    addRecommendation(RobotTypes.LOCAL_SETTING, "[%s]".formatted(settingName), settingName, RobotTailTypes.TAB);
                });
            }
        }
        addRecommendation(RobotTypes.LOCAL_SETTING, "[Return]", RobotNames.RETURN_LOCAL_SETTING_NAME, RobotTailTypes.TAB);

        for (GlobalSettingType importSettingType : importSettingTypes) {
            List<CharSequence> importGlobalSettingNames = typeMappingProvider.getGlobalSettingName(importSettingType);
            if (importGlobalSettingNames != null) {
                importGlobalSettingNames.forEach(importGlobalSettingName -> {
                    String settingName = importGlobalSettingName.toString();
                    addRecommendation(IMPORT, settingName, settingName, RobotTailTypes.TAB);
                });
            }
        }

        for (BehaviourDrivenType behaviourDrivenType : BehaviourDrivenType.getEntries()) {
            List<CharSequence> behaviourDrivenIdentifierName = typeMappingProvider.getBehaviourDrivenIdentifierName(behaviourDrivenType);
            if (behaviourDrivenIdentifierName != null) {
                behaviourDrivenIdentifierName.forEach(identifierName -> {
                    String name = identifierName.toString();
                    addRecommendation(GHERKIN, name, name, RobotTailTypes.SPACE);
                });
            }
        }

        addRecommendation(SYNTAX_MARKER, "IF", "IF", RobotTailTypes.TAB);
        addRecommendation(RobotTypes.CONDITIONAL_STRUCTURE, "ELSE IF", "ELSE IF", RobotTailTypes.TAB);
        addRecommendation(RobotTypes.CONDITIONAL_STRUCTURE, "ELSE", "ELSE", RobotTailTypes.NONE);
        addRecommendation(SYNTAX_MARKER, "WHILE", "WHILE", RobotTailTypes.TAB);
        addRecommendation(SYNTAX_MARKER, "FOR", "FOR", RobotTailTypes.TAB);
        addRecommendation(RobotTypes.LOOP_CONTROL_STRUCTURE, "CONTINUE", "CONTINUE", RobotTailTypes.NONE);
        addRecommendation(RobotTypes.LOOP_CONTROL_STRUCTURE, "BREAK", "BREAK", RobotTailTypes.NONE);
        addRecommendation(SYNTAX_MARKER, "TRY", "TRY", RobotTailTypes.NONE);
        addRecommendation(RobotTypes.EXCEPTION_HANDLING_STRUCTURE, "EXCEPT", "EXCEPT", RobotTailTypes.TAB);
        addRecommendation(RobotTypes.EXCEPTION_HANDLING_STRUCTURE, "ELSE", "ELSE", RobotTailTypes.NONE);
        addRecommendation(RobotTypes.EXCEPTION_HANDLING_STRUCTURE, "FINALLY", "FINALLY", RobotTailTypes.NONE);
        addRecommendation(RobotTypes.USER_KEYWORD_STATEMENT, "RETURN", "RETURN", RobotTailTypes.NONE);
        addRecommendation(RobotTypes.FOR_IN, "IN", "IN", RobotTailTypes.TAB);
        addRecommendation(RobotTypes.FOR_IN, "IN RANGE", "IN RANGE", RobotTailTypes.TAB);
        addRecommendation(RobotTypes.FOR_IN, "IN ENUMERATE", "IN ENUMERATE", RobotTailTypes.TAB);
        addRecommendation(RobotTypes.FOR_IN, "IN ZIP", "IN ZIP", RobotTailTypes.TAB);
        addRecommendation(SYNTAX_MARKER, "AS", "AS", RobotTailTypes.TAB);
        addRecommendation(SYNTAX_MARKER, "VAR", "VAR", RobotTailTypes.TAB);
    }

    private void addRecommendation(@NotNull IElementType elementType, @NotNull String syntax, @NotNull String displayText, @Nullable TailType tailType) {
        keywordTable.addRecommendation(elementType, syntax, displayText, tailType);
    }

    @NotNull
    public Set<RecommendationWord> getRecommendationsForType(IElementType type) {
        return keywordTable.getRecommendationsForType(type);
    }

    @Override
    public void dispose() {
    }
}
