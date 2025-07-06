package dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParameterDto implements DefinedParameter {

    private final PsiElement reference;
    private final String name;
    private final String defaultValue;
    private final boolean keywordContainer;

    public ParameterDto(@NotNull PsiElement reference, String name, String defaultValue) {
        this(reference, name, defaultValue, false);
    }

    public ParameterDto(@NotNull PsiElement reference, String name, String defaultValue, boolean keywordContainer) {
        this.reference = reference;
        this.name = name.trim();
        this.defaultValue = defaultValue != null && !defaultValue.trim().isEmpty() ? defaultValue : null;
        this.keywordContainer = keywordContainer;
    }

    @Nullable
    @Override
    public final String getLookup() {
        return this.name;
    }

    @Nullable
    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isKeywordContainer() {
        return keywordContainer;
    }

    @NotNull
    @Override
    public final PsiElement reference() {
        return reference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ParameterDto variable = (ParameterDto) o;
            return this.name.equals(variable.name);
        } else {
            return false;
        }
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
