package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import dev.xeonkryptos.xeonrobotframeworkplugin.ide.RobotTailTypes;
import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.TailTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class RobotKeywordProvider {

    private static final RobotKeywordTable KEYWORD_TABLE = new RobotKeywordTable();
    private static final Set<String> GLOBAL_SETTINGS = new HashSet<>();
    private static final Set<String> SETTINGS_FOLLOWED_BY_KEYWORDS = new HashSet<>();
    private static final Set<String> SETTINGS_FOLLOWED_BY_STRINGS = new HashSet<>();
    private static final Set<String> SETTINGS_FOLLOWED_BY_VARIABLE_DEFINITIONS = new HashSet<>();

    private RobotKeywordProvider() {
    }

    private static void addRecommendation(@NotNull RobotElementType elementType,
                                          @NotNull String syntax,
                                          @NotNull String displayText,
                                          @Nullable TailType tailType) {
        KEYWORD_TABLE.addRecommendation(elementType, syntax, displayText, tailType);
    }

    public static boolean isGlobalSetting(String word) {
        return GLOBAL_SETTINGS.contains(word);
    }

    public static boolean isSyntaxFollowedByKeyword(String word) {
        return SETTINGS_FOLLOWED_BY_KEYWORDS.contains(word);
    }

    public static boolean isTestTemplate(String word) {
        return "Test Template".contains(word);
    }

    public static boolean isSyntaxFollowedByString(String word) {
        return SETTINGS_FOLLOWED_BY_STRINGS.contains(word);
    }

    public static boolean isSyntaxFollowedByVariableDefinition(String word) {
        return SETTINGS_FOLLOWED_BY_VARIABLE_DEFINITIONS.contains(word);
    }

    public static boolean isSyntaxOfType(RobotElementType type, String word) {
        return KEYWORD_TABLE.getSyntaxOfType(type).contains(word);
    }

    @NotNull
    public static Set<String> getSyntaxOfType(RobotElementType type) {
        return KEYWORD_TABLE.getSyntaxOfType(type);
    }

    @NotNull
    public static Set<RecommendationWord> getRecommendationsForType(RobotElementType type) {
        return KEYWORD_TABLE.getRecommendationsForType(type);
    }

    static {
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Settings ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Setting ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Test Cases ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Test Case ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Keywords ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Keyword ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Variables ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Variable ***");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.HEADING, "*** Tasks ***");

        addRecommendation(RobotTokenTypes.HEADING, "*** Settings ***", "Settings", RobotTailTypes.NEW_LINE);
        addRecommendation(RobotTokenTypes.HEADING, "*** Test Cases ***", "Test Cases", RobotTailTypes.NEW_LINE);
        addRecommendation(RobotTokenTypes.HEADING, "*** Keywords ***", "Keywords", RobotTailTypes.NEW_LINE);
        addRecommendation(RobotTokenTypes.HEADING, "*** Variables ***", "Variables", RobotTailTypes.NEW_LINE);
        addRecommendation(RobotTokenTypes.HEADING, "*** Tasks ***", "Variables", RobotTailTypes.NEW_LINE);

        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Suite Setup");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Suite Teardown");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Test Timeout");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Test Setup");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Test Teardown");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Test Template");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Documentation");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Metadata");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Name");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Force Tags");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Default Tags");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Test Tags");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SETTING, "Keyword Tags");

        for (String syntax : KEYWORD_TABLE.getSyntaxOfType(RobotTokenTypes.SETTING)) {
            addRecommendation(RobotTokenTypes.SETTING, syntax, syntax, RobotTailTypes.TAB);
        }

        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Setup]");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Teardown]");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Arguments]");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Template]");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Documentation]");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Tags]");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Timeout]");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.BRACKET_SETTING, "[Return]");

        addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Documentation]", "Documentation", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Tags]", "Tags", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Setup]", "Setup", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Teardown]", "Teardown", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Template]", "Template", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Timeout]", "Timeout", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Arguments]", "Arguments", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.BRACKET_SETTING, "[Return]", "Return", RobotTailTypes.TAB);

        KEYWORD_TABLE.addSyntax(RobotTokenTypes.IMPORT, "Library");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.IMPORT, "Resource");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.IMPORT, "Variables");

        addRecommendation(RobotTokenTypes.IMPORT, "Library", "Library", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.IMPORT, "Resource", "Resource", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.IMPORT, "Variables", "Variables", RobotTailTypes.TAB);

        KEYWORD_TABLE.addSyntax(RobotTokenTypes.GHERKIN, "Given");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.GHERKIN, "When");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.GHERKIN, "Then");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.GHERKIN, "And");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.GHERKIN, "BUT");

        addRecommendation(RobotTokenTypes.GHERKIN, "Given", "Given", TailTypes.spaceType());
        addRecommendation(RobotTokenTypes.GHERKIN, "When", "When", TailTypes.spaceType());
        addRecommendation(RobotTokenTypes.GHERKIN, "Then", "Then", TailTypes.spaceType());
        addRecommendation(RobotTokenTypes.GHERKIN, "And", "And", TailTypes.spaceType());
        addRecommendation(RobotTokenTypes.GHERKIN, "BUT", "BUT", TailTypes.spaceType());

        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SYNTAX_MARKER, "IF");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SYNTAX_MARKER, "END");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SYNTAX_MARKER, "ELSE");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SYNTAX_MARKER, "ELSE IF");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SYNTAX_MARKER, "FOR");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SYNTAX_MARKER, "WHILE");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SYNTAX_MARKER, "CONTINUE");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SYNTAX_MARKER, "BREAK");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SYNTAX_MARKER, "TRY");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SYNTAX_MARKER, "EXCEPT");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SYNTAX_MARKER, "FINALLY");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SYNTAX_MARKER, "RETURN");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SYNTAX_MARKER, "IN");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SYNTAX_MARKER, "IN RANGE");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SYNTAX_MARKER, "IN ENUMERATE");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SYNTAX_MARKER, "AS");
        KEYWORD_TABLE.addSyntax(RobotTokenTypes.SYNTAX_MARKER, "VAR");

        addRecommendation(RobotTokenTypes.SYNTAX_MARKER, "IF", "IF", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.SYNTAX_MARKER, "END", "END", TailTypes.noneType());
        addRecommendation(RobotTokenTypes.SYNTAX_MARKER, "ELSE", "ELSE", TailTypes.noneType());
        addRecommendation(RobotTokenTypes.SYNTAX_MARKER, "ELSE IF", "ELSE IF", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.SYNTAX_MARKER, "WHILE", "WHILE", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.SYNTAX_MARKER, "CONTINUE", "CONTINUE", TailTypes.noneType());
        addRecommendation(RobotTokenTypes.SYNTAX_MARKER, "BREAK", "BREAK", TailTypes.noneType());
        addRecommendation(RobotTokenTypes.SYNTAX_MARKER, "FOR", "FOR", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.SYNTAX_MARKER, "TRY", "TRY", TailTypes.noneType());
        addRecommendation(RobotTokenTypes.SYNTAX_MARKER, "EXCEPT", "EXCEPT", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.SYNTAX_MARKER, "FINALLY", "FINALLY", TailTypes.noneType());
        addRecommendation(RobotTokenTypes.SYNTAX_MARKER, "RETURN", "RETURN", TailTypes.noneType());
        addRecommendation(RobotTokenTypes.SYNTAX_MARKER, "IN", "IN", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.SYNTAX_MARKER, "IN RANGE", "IN RANGE", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.SYNTAX_MARKER, "IN ENUMERATE", "IN ENUMERATE", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.SYNTAX_MARKER, "AS", "AS", RobotTailTypes.TAB);
        addRecommendation(RobotTokenTypes.SYNTAX_MARKER, "VAR", "VAR", RobotTailTypes.TAB);

        GLOBAL_SETTINGS.addAll(KEYWORD_TABLE.getSyntaxOfType(RobotTokenTypes.SETTING));
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Suite Setup");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Suite Teardown");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Test Setup");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Test Teardown");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("Test Template");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("[Setup]");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("[Teardown]");
        SETTINGS_FOLLOWED_BY_KEYWORDS.add("[Template]");
        SETTINGS_FOLLOWED_BY_STRINGS.add("Documentation");
        SETTINGS_FOLLOWED_BY_STRINGS.add("Metadata");
        SETTINGS_FOLLOWED_BY_STRINGS.add("Name");
        SETTINGS_FOLLOWED_BY_STRINGS.add("Force Tags");
        SETTINGS_FOLLOWED_BY_STRINGS.add("Default Tags");
        SETTINGS_FOLLOWED_BY_STRINGS.add("Keyword Tags");
        SETTINGS_FOLLOWED_BY_STRINGS.add("Test Tags");
        SETTINGS_FOLLOWED_BY_STRINGS.add("Test Timeout");
        SETTINGS_FOLLOWED_BY_STRINGS.add("[Tags]");
        SETTINGS_FOLLOWED_BY_STRINGS.add("[Return]");
        SETTINGS_FOLLOWED_BY_STRINGS.add("[Timeout]");
        SETTINGS_FOLLOWED_BY_STRINGS.add("[Documentation]");
        SETTINGS_FOLLOWED_BY_VARIABLE_DEFINITIONS.add("[Arguments]");
    }
}
