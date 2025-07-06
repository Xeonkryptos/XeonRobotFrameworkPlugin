package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.lang.WhitespaceSkippedCallback;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.psi.tree.IElementType;

import java.util.ArrayList;
import java.util.List;

public class RobotParserUtil extends GeneratedParserUtilBase {

    /**
     * Custom parser to correctly detect and parse positional arguments in Robot Framework. In the space-based format of Robot files you need to separate arguments
     * by at least 2 successive spaces, a tab or a newline. It is quite hard to implement something for every case in the lexer and parser grammar to achieve
     * exactly that without breaking other things, especially multilines. Especially when WHITE_SPACE tokens are skipped automatically.
     * <p>
     * This custom parser implementation is taking care of the whitespace separation detection needed to end a positional argument, especially when the positional
     * argument consists of multiple variables and literal constants.
     *
     * @param builder current PsiBuilder
     * @param level current parser level
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
        exit_section_(builder, m, RobotTypes.LITERAL_CONSTANT_VALUE, r);
        return r;
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
