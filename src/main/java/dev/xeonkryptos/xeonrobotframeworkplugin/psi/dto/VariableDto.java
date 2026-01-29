package dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.VariableNameUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class VariableDto implements DefinedVariable {

    private final PsiElement reference;
    private final String name;
    private final String matchingVariableName;
    private final VariableScope scope;
    private final VariableType variableType;

    private final Set<String> variableNameVariants;

    public VariableDto(@NotNull PsiElement reference, @NotNull String name, @NotNull VariableType variableType, @Nullable VariableScope scope) {
        this(reference, name.trim(), name, variableType, scope);
    }

    public VariableDto(@NotNull PsiElement reference, @NotNull String name, @NotNull String matchingVariableName, VariableType variableType, @Nullable VariableScope scope) {
        this.reference = reference;
        this.name = normalizeName(name.trim());
        this.matchingVariableName = matchingVariableName.trim();
        this.scope = scope;
        this.variableType = variableType;

        this.variableNameVariants = VariableNameUtil.INSTANCE.computeVariableNameVariants(this.matchingVariableName);
    }

    private static String normalizeName(String name) {
        if (name.startsWith(RobotNames.LIST_VARIABLE_TYPE_INDICATOR_PREFIX) || name.startsWith(RobotNames.DICT_VARIABLE_TYPE_INDICATOR_PREFIX)) {
            int index = name.indexOf("__", 2);
            if (index != -1 && index + 2 < name.length()) {
                return name.substring(index + 2);
            }
        }
        return name;
    }

    @Override
    public final boolean matches(@Nullable String text) {
        if (text == null) {
            return false;
        }
        return VariableNameUtil.INSTANCE.matchesVariableName(text, variableNameVariants);
    }

    @Override
    public final boolean isInScope(@NotNull PsiElement position) {
        return this.scope == null || this.scope.isInScope(reference, position);
    }

    @Override
    public VariableScope getScope() {
        return scope;
    }

    @NotNull
    @Override
    public final PsiElement reference() {
        return this.reference;
    }

    @Override
    public @NotNull VariableType getVariableType() {
        return variableType;
    }

    @Nullable
    @Override
    public final String getLookup() {
        return name;
    }

    @Override
    public String getPresentableText() {
        return variableType.prefixed(name);
    }

    @Override
    public String[] getLookupWords() {
        return new String[] { name, matchingVariableName, variableType.prefixed(name), variableType.prefixed(matchingVariableName) };
    }

    @Override
    public boolean isCaseSensitive() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            VariableDto variable = (VariableDto) o;
            return this.name.equals(variable.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
