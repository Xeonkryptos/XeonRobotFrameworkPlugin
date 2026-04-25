package dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto;

import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import com.intellij.psi.PsiElement;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.Collator;

@Builder(builderMethodName = "")
public class ParameterDto implements DefinedParameter {

    private final PsiElement reference;
    private final String name;
    private final String defaultValue;
    private final boolean positionalContainer;
    private final boolean keywordContainer;

    private boolean positionalOnly;

    public static ParameterDtoBuilder builder(PsiElement reference, String name) {
        return new ParameterDtoBuilder().reference(reference).name(name);
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
    public boolean isPositionalOnly() {
        return positionalContainer || positionalOnly;
    }

    @Override
    public void markAsPositionalOnly() {
        this.positionalOnly = true;
    }

    @Override
    public boolean isPositionalContainer() {
        return positionalContainer;
    }

    @Override
    public boolean isKeywordContainer() {
        return keywordContainer;
    }

    @Override
    public boolean matches(@NotNull String parameterName) {
        RobotOptionsProvider robotOptionsProvider = RobotOptionsProvider.getInstance(reference.getProject());
        Collator parameterNameCollator = robotOptionsProvider.getParameterNameCollator();
        return parameterNameCollator.equals(this.name, parameterName);
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
