package dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.PatternUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariableScope;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class VariableDto implements DefinedVariable {

    private final PsiElement reference;
    private final String name;
    private final ReservedVariableScope scope;

    private Pattern pattern;

    public VariableDto(@NotNull PsiElement reference, @NotNull String name, @Nullable ReservedVariableScope scope) {
        this.reference = reference;
        this.name = name.trim();
        this.scope = scope;
    }

    @Override
    public final boolean matches(@Nullable String text) {
        if (text == null) {
            return false;
        } else {
            Pattern pattern = this.pattern;
            if (this.pattern == null && this.name.length() > 3) {
                pattern = Pattern.compile(PatternUtil.getVariablePattern(this.name), Pattern.CASE_INSENSITIVE);
                this.pattern = pattern;
            }
            return pattern != null && pattern.matcher(text).matches();
        }
    }

    @Override
    public final boolean isInScope(@NotNull PsiElement position) {
        return this.scope == null || this.scope.isInScope(reference, position);
    }

    @Nullable
    @Override
    public final PsiElement reference() {
        return this.reference;
    }

    @Nullable
    @Override
    public final String getLookup() {
        return this.scope == null ? this.reference.getText() : this.name;
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
