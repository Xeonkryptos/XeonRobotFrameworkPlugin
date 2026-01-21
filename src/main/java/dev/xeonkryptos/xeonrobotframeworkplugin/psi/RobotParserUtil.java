package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.lang.LighterASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.SyntaxTreeBuilder.Production;
import com.intellij.lang.WhitespaceSkippedCallback;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.openapi.util.Key;
import com.intellij.psi.tree.IElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariable;

import java.util.ArrayList;
import java.util.List;

public class RobotParserUtil extends GeneratedParserUtilBase {

    private static final Key<Boolean> GLOBAL_TEMPLATE_SETTING_KEY = Key.create("GLOBAL_TEMPLATE_SETTING_KEY");
    private static final Key<Boolean> LOCAL_TEMPLATE_SETTING_KEY = Key.create("LOCAL_TEMPLATE_SETTING_KEY");
    private static final Key<Boolean> LOCAL_TEMPLATE_SETTING_RESET_OVERRIDE_KEY = Key.create("LOCAL_TEMPLATE_SETTING_RESET_OVERRIDE_KEY");

    private static final String TEMPLATE_LOCAL_SETTING_NAME = "[Template]";

    public static final Hook<Void> CLEAR_TEMPLATE_STATE_HOOK = (builder, marker, param) -> {
        builder.putUserData(LOCAL_TEMPLATE_SETTING_KEY, false);
        return marker;
    };
    public static final Hook<Void> LOCAL_TEMPLATE_DEFINITION_HOOK = (builder, marker, param) -> {
        LighterASTNode latestDoneMarker = builder.getLatestDoneMarker();
        assert latestDoneMarker != null; // should never be null here as we just finished a successful parse of local setting

        CharSequence originalText = builder.getOriginalText();
        int originalTextLength = originalText.length();

        int settingStartOffset = latestDoneMarker.getStartOffset();
        int settingEndOffset = latestDoneMarker.getEndOffset();
        int settingNameEndOffset = Math.min(originalTextLength, settingStartOffset + TEMPLATE_LOCAL_SETTING_NAME.length());

        String localSettingName = originalText.subSequence(settingStartOffset, settingNameEndOffset).toString().replaceAll("\\s+", "");
        if (TEMPLATE_LOCAL_SETTING_NAME.equalsIgnoreCase(localSettingName)) {
            updateLocalTemplateState(builder, settingStartOffset, settingEndOffset, originalText);
        }
        return marker;
    };

    /**
     * Custom parser to correctly detect and parse positional arguments in Robot Framework. In the space-based format of Robot files you need to separate arguments
     * by at least 2 successive spaces, a tab or a newline. It is quite hard to implement something for every case in the lexer and parser grammar to achieve
     * exactly that without breaking other things, especially multilines. Especially when WHITE_SPACE tokens are skipped automatically.
     * <p>
     * This custom parser implementation is taking care of the whitespace separation detection needed to end a positional argument, especially when the positional
     * argument consists of multiple variables and literal constants.
     *
     * @param builder                  current PsiBuilder
     * @param level                    current parser level
     * @param positionalArgumentParser parser for positional arguments, e.g. {@link RobotParser#positional_argument_content(PsiBuilder, int)}
     *
     * @return true if a positional argument could be parsed (was detected), false otherwise
     */
    public static boolean parsePositionalArgument(PsiBuilder builder, int level, Parser positionalArgumentParser) {
        if (!recursion_guard_(builder, level, "parse_positional_argument")) {
            return false;
        }

        WhitespaceSkippedMemory whitespaceSkippedMemory = new WhitespaceSkippedMemory();
        builder.setWhitespaceSkippedCallback(whitespaceSkippedMemory);

        Marker m = enter_section_(builder);
        boolean r = positionalArgumentParser.parse(builder, level + 1);
        //noinspection LoopConditionNotUpdatedInsideLoop
        while (r) {
            int c = current_position_(builder);
            if (nextTokenIs(builder, RobotTypes.EOL) || whitespaceSkippedMemory.containsArgumentEndMarker(builder)) {
                break;
            }
            if (!positionalArgumentParser.parse(builder, level + 1)) {
                break;
            }
            if (!empty_element_parsed_guard_(builder, "parse_positional_argument", c)) {
                break;
            }
        }

        builder.setWhitespaceSkippedCallback(null);
        exit_section_(builder, m, null, r);
        return r;
    }

    public static boolean parseTemplateStatementsGlobalSetting(PsiBuilder builder, int level, Parser templateStatementsParser) {
        boolean result = templateStatementsParser.parse(builder, level);
        if (result) {
            builder.putUserData(GLOBAL_TEMPLATE_SETTING_KEY, true);
        }
        return result;
    }

    public static boolean parseTestcaseTaskStatement(PsiBuilder builder, int level, Parser templateArgumentsParser, Parser... parsers) {
        if (!recursion_guard_(builder, level, "parse_testcase_task_statement")) {
            return false;
        }
        Boolean globalTemplateDefined = builder.getUserData(GLOBAL_TEMPLATE_SETTING_KEY);
        Boolean localTemplateResetOverride = builder.getUserData(LOCAL_TEMPLATE_SETTING_RESET_OVERRIDE_KEY);
        if (Boolean.TRUE.equals(globalTemplateDefined) && !Boolean.TRUE.equals(localTemplateResetOverride)) {
            builder.putUserData(LOCAL_TEMPLATE_SETTING_KEY, true);
        }
        Boolean localTemplateDefined = builder.getUserData(LOCAL_TEMPLATE_SETTING_KEY);
        if (Boolean.TRUE.equals(localTemplateDefined)) {
            return templateArgumentsParser.parse(builder, level + 1);
        }
        for (Parser parser : parsers) {
            boolean result = parser.parse(builder, level + 1);
            if (result) {
                return true;
            }
        }
        return false;
    }

    private static void updateLocalTemplateState(PsiBuilder builder, int settingStartOffset, int settingEndOffset, CharSequence originalText) {
        builder.putUserData(LOCAL_TEMPLATE_SETTING_KEY, false);
        builder.putUserData(LOCAL_TEMPLATE_SETTING_RESET_OVERRIDE_KEY, true);
        for (Production production : builder.getProductions()) {
            int productionStartOffset = production.getStartOffset();
            if (productionStartOffset >= settingStartOffset) {
                IElementType tokenType = production.getTokenType();
                if (tokenType == RobotTypes.KEYWORD_CALL || tokenType == RobotTypes.VARIABLE) {
                    int productionEndOffset = production.getEndOffset();
                    if (productionEndOffset <= settingEndOffset) {
                        builder.putUserData(LOCAL_TEMPLATE_SETTING_RESET_OVERRIDE_KEY, false);
                        if (tokenType == RobotTypes.KEYWORD_CALL) {
                            builder.putUserData(LOCAL_TEMPLATE_SETTING_KEY, true);
                        } else {
                            String variableText = originalText.subSequence(productionStartOffset, productionEndOffset).toString();
                            boolean localTemplateDetected = !variableText.equals(ReservedVariable.EMPTY.getVariable());
                            builder.putUserData(LOCAL_TEMPLATE_SETTING_KEY, localTemplateDetected);
                        }
                        break;
                    }
                }
            }
        }
    }

    private static class WhitespaceSkippedMemory implements WhitespaceSkippedCallback {

        private final List<SkippedWhitespaceRange> skippedWhitespaceRanges = new ArrayList<>();

        @Override
        public void onSkip(IElementType token, int start, int stop) {
            SkippedWhitespaceRange skippedWhitespaceRange = new SkippedWhitespaceRange(start, stop);
            skippedWhitespaceRanges.add(skippedWhitespaceRange);
        }

        boolean containsArgumentEndMarker(PsiBuilder builder) {
            for (SkippedWhitespaceRange range : skippedWhitespaceRanges) {
                if (range.isEndOfArgumentMarker(builder)) {
                    return true;
                }
            }
            return false;
        }
    }

    private record SkippedWhitespaceRange(int start, int stop) {

        boolean isEndOfArgumentMarker(PsiBuilder builder) {
            if (stop - start >= 2) {
                return true;
            }
            char whitespaceChar = builder.getOriginalText().charAt(start);
            return whitespaceChar == '\t' || whitespaceChar == '\n' || whitespaceChar == '\r';
        }
    }
}
