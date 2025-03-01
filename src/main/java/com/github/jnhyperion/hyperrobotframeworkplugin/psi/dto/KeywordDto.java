package com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedKeyword;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedParameter;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.PatternBuilder;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.PatternUtil;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.ReservedVariable;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyBoolLiteralExpression;
import com.jetbrains.python.psi.PyNoneLiteralExpression;
import com.jetbrains.python.psi.PyParameter;
import com.jetbrains.python.psi.PyReferenceExpression;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KeywordDto implements DefinedKeyword {

    private final PsiElement reference;
    private final String name;
    private final boolean args;
    private final Pattern namePattern;
    private final List<PyParameter> parameters;

    public KeywordDto(@NotNull PsiElement reference, @NotNull String namespace, @NotNull String name) {
        this(reference, namespace, name, null);
    }

    public KeywordDto(@NotNull PsiElement reference, @NotNull String namespace, @NotNull String name, List<PyParameter> parameters) {
        this.reference = reference;
        this.name = PatternUtil.functionToKeyword(name).trim();
        this.namePattern = Pattern.compile(PatternBuilder.parseNamespace(namespace, PatternUtil.keywordToFunction(this.name)), Pattern.CASE_INSENSITIVE);
        if (parameters != null) {
            this.args = parameters.stream().anyMatch(parameter -> !parameter.isSelf() && parameter.isPhysical() && parameter.getName() != null);
        } else {
            this.args = false;
        }
        this.parameters = parameters;
    }

    @Override
    public final String getKeywordName() {
        return this.name;
    }

    @Override
    public final boolean hasParameters() {
        return this.args;
    }

    @Override
    public Collection<DefinedParameter> getParameters() {
        if (parameters != null) {
            return parameters.stream()
                             .filter(parameter -> !parameter.isSelf() && parameter.isPhysical() && parameter.getName() != null)
                             .map(parameter -> {
                                 String defaultValue = null;
                                 if (parameter.hasDefaultValue()) {
                                     defaultValue = extractDefaultValue(parameter);
                                 }
                                 return new ParameterDto(parameter, parameter.getName(), defaultValue);
                             })
                             .collect(Collectors.toSet());
        }
        return Collections.emptyList();
    }

    @Nullable
    private static String extractDefaultValue(PyParameter parameter) {
        String defaultValue;
        if (parameter.getDefaultValue() instanceof PyStringLiteralExpression expression) {
            defaultValue = expression.getStringValue();
        } else if (parameter.getDefaultValue() instanceof PyBoolLiteralExpression expression) {
            defaultValue = expression.getValue() ? ReservedVariable.TRUE.getVariable() : ReservedVariable.FALSE.getVariable();
        } else if (parameter.getDefaultValue() instanceof PyReferenceExpression) {
            defaultValue = "${%s}".formatted(parameter.getText());
        } else if (parameter.getDefaultValue() instanceof PyNoneLiteralExpression) {
            defaultValue = ReservedVariable.NONE.getVariable();
        } else {
           defaultValue = parameter.getDefaultValueText();
        }
        return defaultValue;
    }

    @Override
    public final boolean matches(String text) {
        return text != null && this.namePattern.matcher(PatternUtil.keywordToFunction(text).trim()).matches();
    }

    @Override
    public final PsiElement reference() {
        return this.reference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            return this.name.equals(((KeywordDto) o).name);
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
        return this.name;
    }
}
