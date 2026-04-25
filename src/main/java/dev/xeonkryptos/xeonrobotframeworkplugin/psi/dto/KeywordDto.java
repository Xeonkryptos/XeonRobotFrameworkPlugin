package dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto;

import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyParameter;
import com.jetbrains.python.psi.PyParameterList;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.PyFunctionParametersVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.DeprecationInspector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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

    public KeywordDto(@NotNull PsiElement reference, @Nullable String libraryName, @NotNull String name, PyParameterList parameterList) {
        this(reference, libraryName, name, convertPyParameters(parameterList), false);
    }

    public KeywordDto(@NotNull PsiElement reference, @Nullable String libraryName, @NotNull String name, Collection<DefinedParameter> parameters, boolean markedAsPrivate) {
        this.reference = reference;
        this.libraryName = libraryName;
        this.name = name.trim();
        this.reducedKeywordName = this.name.toLowerCase().replaceAll("[_\\s]", "");
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

    private static Collection<DefinedParameter> convertPyParameters(PyParameterList parameterList) {
        PyFunctionParametersVisitor visitor = new PyFunctionParametersVisitor();
        parameterList.acceptChildren(visitor);
        return visitor.getDefinedParameters();
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
}
