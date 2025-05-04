package com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedKeyword;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedParameter;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedVariable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.PyElementDeprecatedVisitor;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.PyElementParentTraversalVisitor;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.PatternBuilder;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.PatternUtil;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.ReservedVariable;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyBoolLiteralExpression;
import com.jetbrains.python.psi.PyElement;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyNoneLiteralExpression;
import com.jetbrains.python.psi.PyParameter;
import com.jetbrains.python.psi.PyReferenceExpression;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class KeywordDto implements DefinedKeyword {

    private final PsiElement reference;
    private final String name;
    private final boolean args;
    private final Pattern namePattern;
    private final Collection<DefinedParameter> parameters;
    private final boolean deprecated;

    public KeywordDto(@NotNull PsiElement reference, @NotNull String namespace, @NotNull String name) {
        this(reference, namespace, name, null);
    }

    // Used for user defined keywords
    public KeywordDto(@NotNull PsiElement reference, @NotNull String name, @Nullable Collection<DefinedParameter> parameters) {
        this(reference, "", name, parameters);
    }

    public KeywordDto(@NotNull PsiElement reference, @NotNull String namespace, @NotNull String name, Collection<PyParameter> parameters) {
        this(reference, namespace, name, convertPyParameters(parameters));
    }

    public KeywordDto(@NotNull PsiElement reference,
                      @NotNull String namespace,
                      @NotNull String name,
                      Collection<DefinedParameter> parameters,
                      Object... ignored) { // Object... ignored is just a trick to avoid constructor clashing
        this.reference = reference;
        this.name = PatternUtil.functionToKeyword(name).trim();
        this.namePattern = Pattern.compile(PatternBuilder.parseNamespace(namespace, PatternUtil.keywordToFunction(this.name)), Pattern.CASE_INSENSITIVE);
        this.args = parameters != null && !parameters.isEmpty();
        this.parameters = parameters;
        if (reference instanceof PyElement) {
            PyElementDeprecatedVisitor deprecationVisitor = new PyElementDeprecatedVisitor();
            PyElementVisitor pyElementParentTraversalVisitor = new PyElementParentTraversalVisitor(deprecationVisitor);
            reference.accept(pyElementParentTraversalVisitor);
            deprecated = deprecationVisitor.isDeprecated();
        } else {
            deprecated = false;
        }
    }

    @Override
    public final String getKeywordName() {
        return name;
    }

    @Override
    public final boolean hasParameters() {
        return args;
    }

    @Override
    public Collection<DefinedParameter> getParameters() {
        if (parameters != null) {
            return parameters;
        }
        return Collections.emptyList();
    }

    private static Collection<DefinedParameter> convertPyParameters(Collection<PyParameter> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return Collections.emptyList();
        }
        return parameters.stream()
                         .filter(parameter -> !parameter.isSelf())
                         .map(PyParameter::getAsNamed)
                         .filter(Objects::nonNull)
                         .filter(parameter -> !parameter.isKeywordContainer() && !parameter.isPositionalContainer() && parameter.getName() != null)
                         .map(parameter -> {
                             String defaultValue = null;
                             if (parameter.hasDefaultValue()) {
                                 defaultValue = extractDefaultValue(parameter);
                             }
                             return new ParameterDto(parameter, parameter.getName(), defaultValue);
                         })
                         .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Nullable
    private static String extractDefaultValue(PyParameter parameter) {
        String defaultValue;
        PyDefaultValueExtractor pyDefaultValueExtractor = new PyDefaultValueExtractor();
        PyExpression defaultValueElement = parameter.getDefaultValue();
        if (defaultValueElement != null) {
            defaultValueElement.accept(pyDefaultValueExtractor);
        }
        defaultValue = pyDefaultValueExtractor.defaultValue;
        if (defaultValue == null) {
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

    @Nullable
    public String getArgumentsDisplayable() {
        if (!hasParameters()) {
            return null;
        }
        return formatArguments(getParameters());
    }

    private String formatArguments(Collection<?> arguments) {
        List<String> argumentNames = new ArrayList<>();
        for (Object argument : arguments) {
            if (argument instanceof PyParameter parameter) {
                String name = parameter.getName();
                if (!parameter.isSelf()) {
                    argumentNames.add(name);
                }
            } else if (argument instanceof DefinedVariable definedVariable) {
                argumentNames.add(definedVariable.getLookup());
            }
        }
        return " (" + String.join(", ", argumentNames) + ")";
    }

    @Override
    public boolean isDeprecated() {
        return deprecated;
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

    private static class PyDefaultValueExtractor extends PyElementVisitor {

        private String defaultValue;

        @Override
        public void visitPyStringLiteralExpression(@NotNull PyStringLiteralExpression node) {
            defaultValue = node.getStringValue();
        }

        @Override
        public void visitPyBoolLiteralExpression(@NotNull PyBoolLiteralExpression node) {
            defaultValue = node.getValue() ? ReservedVariable.TRUE.getVariable() : ReservedVariable.FALSE.getVariable();
        }

        @Override
        public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
            defaultValue = node.getName();
        }

        @Override
        public void visitPyNoneLiteralExpression(@NotNull PyNoneLiteralExpression node) {
            defaultValue = ReservedVariable.NONE.getVariable();
        }
    }
}
