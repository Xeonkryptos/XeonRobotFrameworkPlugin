package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.ParameterDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.VariableDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.PatternUtil;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyNamedParameter;
import com.jetbrains.python.psi.PyParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class KeywordStatementImpl extends RobotPsiElementBase implements KeywordStatement {

    private List<Argument> arguments;
    private List<NamedArgument> namedArguments;
    private List<PositionalArgument> positionalArguments;
    private DefinedVariable variable;
    private KeywordInvokable invokable;
    private Collection<DefinedParameter> availableKeywordParameters;

    public KeywordStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @Override
    public final KeywordInvokable getInvokable() {
        KeywordInvokable result = this.invokable;
        if (this.invokable == null) {
            for (PsiElement child : getChildren()) {
                if (child instanceof KeywordInvokable) {
                    result = (KeywordInvokable) child;
                    break;
                }
            }
            this.invokable = result;
        }
        return result;
    }

    @NotNull
    @Override
    public List<Argument> getArguments() {
        List<Argument> arguments = this.arguments;
        if (arguments == null) {
            arguments = PsiTreeUtil.getChildrenOfTypeAsList(this, Argument.class);
            this.arguments = arguments;
        }
        return arguments;
    }

    @Override
    public @NotNull List<NamedArgument> getNamedArguments() {
        List<NamedArgument> results = this.namedArguments;
        if (this.namedArguments == null) {
            results = new ArrayList<>(PsiTreeUtil.getChildrenOfTypeAsList(this, NamedArgument.class));
            this.namedArguments = results;
        }
        return results;
    }

    @NotNull
    @Override
    public final List<PositionalArgument> getPositionalArguments() {
        List<PositionalArgument> results = this.positionalArguments;
        if (this.positionalArguments == null) {
            results = new ArrayList<>(PsiTreeUtil.getChildrenOfTypeAsList(this, PositionalArgument.class));
            this.positionalArguments = results;
        }
        return results;
    }

    @NotNull
    @Override
    public Collection<DefinedParameter> getAvailableParameters() {
        Collection<DefinedParameter> localDefinedParameters = this.availableKeywordParameters;
        if (this.availableKeywordParameters == null) {
            localDefinedParameters = collectKeywordParameters();
            this.availableKeywordParameters = localDefinedParameters;
        }
        return localDefinedParameters;
    }

    @Nullable
    @Override
    public final DefinedVariable getGlobalVariable() {
        DefinedVariable result = this.variable;
        if (result == null) {
            KeywordInvokable invokable = getInvokable();
            if (invokable != null) {
                String text = invokable.getPresentableText();
                if (PatternUtil.isVariableSettingKeyword(text)) {
                    List<PositionalArgument> positionalArguments = getPositionalArguments();
                    if (!positionalArguments.isEmpty()) {
                        PositionalArgument variable = positionalArguments.get(0);
                        // already formatted ${X}
                        result = new VariableDto(variable, variable.getPresentableText(), null);
                    }
                }
            }
            this.variable = result;
        }
        return result;
    }

    @NotNull
    private Collection<DefinedParameter> collectKeywordParameters() {
        Set<DefinedParameter> results = new LinkedHashSet<>();
        Optional<PsiElement> resolvedReferenceOpt = Optional.ofNullable(PsiTreeUtil.findChildOfType(this, KeywordInvokable.class))
                                                            .map(KeywordInvokable::getReference)
                                                            .map(PsiReference::resolve);
        if (resolvedReferenceOpt.isPresent()) {
            PsiElement psiElement = resolvedReferenceOpt.get();
            if (psiElement instanceof PyFunction pyFunction) {
                PyParameter[] parameters = pyFunction.getParameterList().getParameters();
                for (PyParameter parameter : parameters) {
                    PyNamedParameter parameterName = parameter.getAsNamed();
                    if (parameterName != null && !parameterName.isSelf() && !parameterName.isPositionalContainer() && !parameterName.isKeywordContainer()) {
                        String defaultValueText = parameter.getDefaultValueText();
                        results.add(new ParameterDto(parameter, parameterName.getRepr(false), defaultValueText));
                    }
                }
            }
        }
        return results;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        this.namedArguments = null;
        this.positionalArguments = null;
        this.arguments = null;
        this.invokable = null;
        this.variable = null;
        this.availableKeywordParameters = null;
    }
}
