package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.TailTypes;
import com.intellij.psi.tree.IElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotTailTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class RobotKeywordProvider {

    public static final IElementType IMPORT = new RobotElementType("IMPORT");
    public static final IElementType GHERKIN = new RobotElementType("GHERKIN");
    public static final IElementType SYNTAX_MARKER = new RobotElementType("SYNTAX_MARKER");

    private static final RobotKeywordTable KEYWORD_TABLE = new RobotKeywordTable();

    private RobotKeywordProvider() {
    }

    private static void addRecommendation(@NotNull IElementType elementType,
                                          @NotNull String syntax,
                                          @NotNull String displayText,
                                          @Nullable TailType tailType) {
        KEYWORD_TABLE.addRecommendation(elementType, syntax, displayText, tailType);
    }

    @NotNull
    public static Set<RecommendationWord> getRecommendationsForType(IElementType type) {
        return KEYWORD_TABLE.getRecommendationsForType(type);
    }

    static {
        KEYWORD_TABLE.addSyntax(RobotTypes.SECTION, "*** Settings ***");
        KEYWORD_TABLE.addSyntax(RobotTypes.SECTION, "*** Setting ***");
        KEYWORD_TABLE.addSyntax(RobotTypes.SECTION, "*** Test Cases ***");
        KEYWORD_TABLE.addSyntax(RobotTypes.SECTION, "*** Test Case ***");
        KEYWORD_TABLE.addSyntax(RobotTypes.SECTION, "*** Keywords ***");
        KEYWORD_TABLE.addSyntax(RobotTypes.SECTION, "*** Keyword ***");
        KEYWORD_TABLE.addSyntax(RobotTypes.SECTION, "*** Variables ***");
        KEYWORD_TABLE.addSyntax(RobotTypes.SECTION, "*** Variable ***");
        KEYWORD_TABLE.addSyntax(RobotTypes.SECTION, "*** Tasks ***");

        addRecommendation(RobotTypes.SECTION, "*** Settings ***", "Settings", RobotTailTypes.NEW_LINE);
        addRecommendation(RobotTypes.SECTION, "*** Test Cases ***", "Test Cases", RobotTailTypes.NEW_LINE);
        addRecommendation(RobotTypes.SECTION, "*** Keywords ***", "Keywords", RobotTailTypes.NEW_LINE);
        addRecommendation(RobotTypes.SECTION, "*** Variables ***", "Variables", RobotTailTypes.NEW_LINE);
        addRecommendation(RobotTypes.SECTION, "*** Tasks ***", "Variables", RobotTailTypes.NEW_LINE);

        KEYWORD_TABLE.addSyntax(RobotTypes.GLOBAL_SETTING_STATEMENT, "Suite Setup");
        KEYWORD_TABLE.addSyntax(RobotTypes.GLOBAL_SETTING_STATEMENT, "Suite Teardown");
        KEYWORD_TABLE.addSyntax(RobotTypes.GLOBAL_SETTING_STATEMENT, "Test Timeout");
        KEYWORD_TABLE.addSyntax(RobotTypes.GLOBAL_SETTING_STATEMENT, "Test Setup");
        KEYWORD_TABLE.addSyntax(RobotTypes.GLOBAL_SETTING_STATEMENT, "Test Teardown");
        KEYWORD_TABLE.addSyntax(RobotTypes.GLOBAL_SETTING_STATEMENT, "Test Template");
        KEYWORD_TABLE.addSyntax(RobotTypes.GLOBAL_SETTING_STATEMENT, "Documentation");
        KEYWORD_TABLE.addSyntax(RobotTypes.GLOBAL_SETTING_STATEMENT, "Metadata");
        KEYWORD_TABLE.addSyntax(RobotTypes.GLOBAL_SETTING_STATEMENT, "Name");
        KEYWORD_TABLE.addSyntax(RobotTypes.GLOBAL_SETTING_STATEMENT, "Force Tags");
        KEYWORD_TABLE.addSyntax(RobotTypes.GLOBAL_SETTING_STATEMENT, "Default Tags");
        KEYWORD_TABLE.addSyntax(RobotTypes.GLOBAL_SETTING_STATEMENT, "Test Tags");
        KEYWORD_TABLE.addSyntax(RobotTypes.GLOBAL_SETTING_STATEMENT, "Keyword Tags");

        for (String syntax : KEYWORD_TABLE.getSyntaxOfType(RobotTypes.GLOBAL_SETTING_STATEMENT)) {
            addRecommendation(RobotTypes.GLOBAL_SETTING_STATEMENT, syntax, syntax, RobotTailTypes.TAB);
        }

        KEYWORD_TABLE.addSyntax(RobotTypes.LOCAL_SETTING, "[Setup]");
        KEYWORD_TABLE.addSyntax(RobotTypes.LOCAL_SETTING, "[Teardown]");
        KEYWORD_TABLE.addSyntax(RobotTypes.LOCAL_SETTING, "[Arguments]");
        KEYWORD_TABLE.addSyntax(RobotTypes.LOCAL_SETTING, "[Template]");
        KEYWORD_TABLE.addSyntax(RobotTypes.LOCAL_SETTING, "[Documentation]");
        KEYWORD_TABLE.addSyntax(RobotTypes.LOCAL_SETTING, "[Tags]");
        KEYWORD_TABLE.addSyntax(RobotTypes.LOCAL_SETTING, "[Timeout]");
        KEYWORD_TABLE.addSyntax(RobotTypes.LOCAL_SETTING, "[Return]");

        addRecommendation(RobotTypes.LOCAL_SETTING, "[Documentation]", RobotNames.DOCUMENTATION_LOCAL_SETTING_NAME, RobotTailTypes.TAB);
        addRecommendation(RobotTypes.LOCAL_SETTING, "[Tags]", RobotNames.TAGS_LOCAL_SETTING_NAME, RobotTailTypes.TAB);
        addRecommendation(RobotTypes.LOCAL_SETTING, "[Setup]", "Setup", RobotTailTypes.TAB);
        addRecommendation(RobotTypes.LOCAL_SETTING, "[Teardown]", RobotNames.TEARDOWN_LOCAL_SETTING_NAME, RobotTailTypes.TAB);
        addRecommendation(RobotTypes.LOCAL_SETTING, "[Template]", RobotNames.TEMPLATE_LOCAL_SETTING_NAME, RobotTailTypes.TAB);
        addRecommendation(RobotTypes.LOCAL_SETTING, "[Timeout]", "Timeout", RobotTailTypes.TAB);
        addRecommendation(RobotTypes.LOCAL_SETTING, "[Arguments]", "Arguments", RobotTailTypes.TAB);
        addRecommendation(RobotTypes.LOCAL_SETTING, "[Return]", RobotNames.RETURN_LOCAL_SETTING_NAME, RobotTailTypes.TAB);

        KEYWORD_TABLE.addSyntax(IMPORT, "Library");
        KEYWORD_TABLE.addSyntax(IMPORT, "Resource");
        KEYWORD_TABLE.addSyntax(IMPORT, "Variables");

        addRecommendation(IMPORT, "Library", "Library", RobotTailTypes.TAB);
        addRecommendation(IMPORT, "Resource", "Resource", RobotTailTypes.TAB);
        addRecommendation(IMPORT, "Variables", "Variables", RobotTailTypes.TAB);

        KEYWORD_TABLE.addSyntax(GHERKIN, "GIVEN");
        KEYWORD_TABLE.addSyntax(GHERKIN, "WHEN");
        KEYWORD_TABLE.addSyntax(GHERKIN, "THEN");
        KEYWORD_TABLE.addSyntax(GHERKIN, "AND");
        KEYWORD_TABLE.addSyntax(GHERKIN, "BUT");

        addRecommendation(GHERKIN, "GIVEN", "Given", TailTypes.spaceType());
        addRecommendation(GHERKIN, "WHEN", "WHEN", TailTypes.spaceType());
        addRecommendation(GHERKIN, "THEN", "THEN", TailTypes.spaceType());
        addRecommendation(GHERKIN, "AND", "AND", TailTypes.spaceType());
        addRecommendation(GHERKIN, "BUT", "BUT", TailTypes.spaceType());

        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "IF");
        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "END");
        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "ELSE");
        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "ELSE IF");
        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "FOR");
        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "WHILE");
        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "CONTINUE");
        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "BREAK");
        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "TRY");
        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "EXCEPT");
        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "FINALLY");
        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "RETURN");
        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "IN");
        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "IN RANGE");
        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "IN ENUMERATE");
        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "IN ZIP");
        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "AS");
        KEYWORD_TABLE.addSyntax(SYNTAX_MARKER, "VAR");

        addRecommendation(SYNTAX_MARKER, "IF", "IF", RobotTailTypes.TAB);
        addRecommendation(RobotTypes.CONDITIONAL_STRUCTURE, "ELSE IF", "ELSE IF", RobotTailTypes.TAB);
        addRecommendation(RobotTypes.CONDITIONAL_STRUCTURE, "ELSE", "ELSE", TailTypes.noneType());
        addRecommendation(SYNTAX_MARKER, "WHILE", "WHILE", RobotTailTypes.TAB);
        addRecommendation(SYNTAX_MARKER, "FOR", "FOR", RobotTailTypes.TAB);
        addRecommendation(RobotTypes.LOOP_CONTROL_STRUCTURE, "CONTINUE", "CONTINUE", TailTypes.noneType());
        addRecommendation(RobotTypes.LOOP_CONTROL_STRUCTURE, "BREAK", "BREAK", TailTypes.noneType());
        addRecommendation(SYNTAX_MARKER, "TRY", "TRY", TailTypes.noneType());
        addRecommendation(RobotTypes.EXCEPTION_HANDLING_STRUCTURE, "EXCEPT", "EXCEPT", RobotTailTypes.TAB);
        addRecommendation(RobotTypes.EXCEPTION_HANDLING_STRUCTURE, "ELSE", "ELSE", TailTypes.noneType());
        addRecommendation(RobotTypes.EXCEPTION_HANDLING_STRUCTURE, "FINALLY", "FINALLY", TailTypes.noneType());
        addRecommendation(RobotTypes.USER_KEYWORD_STATEMENT, "RETURN", "RETURN", TailTypes.noneType());
        addRecommendation(RobotTypes.FOR_IN, "IN", "IN", RobotTailTypes.TAB);
        addRecommendation(RobotTypes.FOR_IN, "IN RANGE", "IN RANGE", RobotTailTypes.TAB);
        addRecommendation(RobotTypes.FOR_IN, "IN ENUMERATE", "IN ENUMERATE", RobotTailTypes.TAB);
        addRecommendation(RobotTypes.FOR_IN, "IN ZIP", "IN ZIP", RobotTailTypes.TAB);
        addRecommendation(SYNTAX_MARKER, "AS", "AS", RobotTailTypes.TAB);
        addRecommendation(SYNTAX_MARKER, "VAR", "VAR", RobotTailTypes.TAB);
    }
}
