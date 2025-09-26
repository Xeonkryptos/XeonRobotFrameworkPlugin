package dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyBoolLiteralExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyNoneLiteralExpression;
import com.jetbrains.python.psi.PyParameter;
import com.jetbrains.python.psi.PyReferenceExpression;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.KeywordUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.DeprecationInspector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
public class KeywordDto implements DefinedKeyword {

    private final PsiElement reference;
    private final String libraryName;
    private final String name;
    private final String reducedKeywordName;
    private final boolean args;
    private final Collection<DefinedParameter> parameters;
    private final boolean deprecated;
    private final boolean markedAsPrivate;

    public KeywordDto(@NotNull PsiElement reference, @Nullable String libraryName, @NotNull String name) {
        this(reference, libraryName, name, null, false);
    }

    public KeywordDto(@NotNull PsiElement reference, @Nullable String libraryName, @NotNull String name, Collection<PyParameter> parameters) {
        this(reference, libraryName, name, convertPyParameters(parameters), false);
    }

    public KeywordDto(@NotNull PsiElement reference,
                      @Nullable String libraryName,
                      @NotNull String name,
                      Collection<DefinedParameter> parameters,
                      boolean markedAsPrivate,
                      Object... ignored) { // Object... ignored is just a trick to avoid constructor clashing
        this.reference = reference;
        this.libraryName = libraryName;
        Project project = reference.getProject();
        this.name = KeywordUtil.getInstance(project).functionToKeyword(name).trim();
        this.reducedKeywordName = name.toLowerCase().replaceAll("[_\\s]", "");
        this.args = parameters != null && !parameters.isEmpty();
        this.parameters = parameters;
        this.deprecated = DeprecationInspector.isDeprecated(reference);
        this.markedAsPrivate = markedAsPrivate;
    }

    @Nullable
    @Override
    public String getLibraryName() {
        return libraryName;
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
        if (text == null) {
            return false;
        }
        String keywordToFunction = text.toLowerCase().replaceAll("[_\\s]", "");
        return reducedKeywordName.equalsIgnoreCase(keywordToFunction);
    }

    @NotNull
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
    public boolean isPrivate() {
        return markedAsPrivate;
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
