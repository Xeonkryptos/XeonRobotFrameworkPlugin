package dev.xeonkryptos.xeonrobotframeworkplugin.psi.util;

import org.jetbrains.annotations.Nullable;

public class PatternUtil {

    private static final String SPACE = " ";
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
}
