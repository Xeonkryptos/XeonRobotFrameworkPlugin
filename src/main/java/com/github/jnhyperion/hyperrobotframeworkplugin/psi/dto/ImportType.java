package com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TreeMap;

public enum ImportType {
    RESOURCE, LIBRARY, VARIABLES, UNKNOWN;

    private static final Map<String, ImportType> MAP;

    static {
        MAP = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        MAP.put("resource", RESOURCE);
        MAP.put("resources", RESOURCE);
        MAP.put("library", LIBRARY);
        MAP.put("libraries", LIBRARY);
        MAP.put("variable", VARIABLES);
        MAP.put("variables", VARIABLES);
    }

    @NotNull
    public static ImportType getType(@Nullable String text) {
        ImportType result = MAP.get(text == null ? null : text.trim());
        return result == null ? UNKNOWN : result;
    }
}
