package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import org.jetbrains.annotations.NotNull;

public interface VariableName {

    default boolean isEmpty() {
        // A variable contains always $, @ or % and at enclosing brackets. Meaning, a variable consists of at least 3 characters even when no name is defined
        return getName().length() <= 3;
    }

    default boolean isEnvironmentVariable() {
        return getName().startsWith("%");
    }

    @NotNull
    String getName();

    @NotNull
    default String getUnwrappedName() {
        if (isEmpty()) {
            return "";
        }
        String name = getName();
        return name.substring(2, name.length() - 1);
    }
}
