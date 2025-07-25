package dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariableScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VariableDto implements DefinedVariable {

    private final PsiElement reference;
    private final String name;
    private final String matchingVariableName;
    private final ReservedVariableScope scope;

    public VariableDto(@NotNull PsiElement reference, @NotNull String name, @Nullable ReservedVariableScope scope) {
        this(reference, name, name, scope);
    }

    public VariableDto(@NotNull PsiElement reference, @NotNull String name, @NotNull String matchingVariableName, @Nullable ReservedVariableScope scope) {
        this.reference = reference;
        this.name = name.trim();
        this.matchingVariableName = matchingVariableName.trim();
        this.scope = scope;
    }

    @Override
    public final boolean matches(@Nullable String text) {
        if (text == null) {
            return false;
        }
        return matchingVariableName.equalsIgnoreCase(text.trim());
    }

    @Override
    public final boolean isInScope(@NotNull PsiElement position) {
        return this.scope == null || this.scope.isInScope(reference, position);
    }

    @NotNull
    @Override
    public final PsiElement reference() {
        return this.reference;
    }

    @Nullable
    @Override
    public final String getLookup() {
        return matchingVariableName;
    }

    @Override
    public String getPresentableText() {
        return this.scope == null ? this.reference.getText() : this.name;
    }

    @Override
    public String[] getLookupWords() {
        return scope != null ? new String[] { matchingVariableName } : EMPTY_LOOKUP_WORDS;
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
