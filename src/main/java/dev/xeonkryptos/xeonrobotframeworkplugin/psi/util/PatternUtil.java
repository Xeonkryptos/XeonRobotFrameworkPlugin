package dev.xeonkryptos.xeonrobotframeworkplugin.psi.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PatternUtil {

    private static final String EMPTY = "";
    private static final String SPACE = " ";
    private static final String SUPER_SPACE = "  ";
    private static final String TAB = "\t";
    private static final String NEWLINE = "\n";
    private static final String UNDERSCORE = "_";

    private PatternUtil() {
    }

    @Nullable
    public static String functionToKeyword(@Nullable String function) {
        return function == null ? null : function.replaceAll(UNDERSCORE, SPACE).trim();
    }

    @Nullable
    public static String keywordToFunction(@Nullable String keyword) {
        return keyword == null ? null : keyword.replaceAll(SPACE, UNDERSCORE).trim();
    }

    @NotNull
    public static String getPresentableText(@Nullable String text) {
        if (text == null) {
            return EMPTY;
        } else {
            int newLine = indexOf(text, NEWLINE);
            int tab = indexOf(text, TAB);
            int superSpace = indexOf(text, SUPER_SPACE);
            newLine = Math.min(Math.min(newLine, tab), superSpace);
            return text.substring(0, newLine).trim();
        }
    }

    private static int indexOf(@NotNull String text, @NotNull String string) {
        int index = text.indexOf(string);
        return index < 0 ? text.length() : index;
    }
}
