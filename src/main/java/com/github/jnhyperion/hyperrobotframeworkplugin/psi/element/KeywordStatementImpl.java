package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.VariableDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.PatternUtil;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.ReservedVariableScope;
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

    private List<Parameter> parameters;
    private List<Argument> arguments;
    private DefinedVariable variable;
    private KeywordInvokable invokable;
    private Collection<DefinedVariable> availableKeywordParameters;

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

    @Override
    public @NotNull List<Parameter> getParameters() {
        List<Parameter> results = this.parameters;
        if (this.parameters == null) {
            results = new ArrayList<>(PsiTreeUtil.getChildrenOfTypeAsList(this, Parameter.class));
            this.parameters = results;
        }
        return results;
    }

    @NotNull
    @Override
    public final List<Argument> getArguments() {
        List<Argument> results = this.arguments;
        if (this.arguments == null) {
            results = new ArrayList<>(PsiTreeUtil.getChildrenOfTypeAsList(this, Argument.class));
            this.arguments = results;
        }
        return results;
    }

    @NotNull
    @Override
    public Collection<DefinedVariable> getAvailableParameters() {
        Collection<DefinedVariable> localDefinedParameters = this.availableKeywordParameters;
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
                    List<Argument> arguments = getArguments();
                    if (!arguments.isEmpty()) {
                        Argument variable = arguments.get(0);
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
    private Collection<DefinedVariable> collectKeywordParameters() {
        Set<DefinedVariable> results = new LinkedHashSet<>();
        Optional<PsiElement> resolvedReferenceOpt = Optional.ofNullable(PsiTreeUtil.findChildOfType(this, KeywordInvokable.class))
                                                            .map(KeywordInvokable::getReference)
                                                            .map(PsiReference::resolve);
        if (resolvedReferenceOpt.isPresent()) {
            PsiElement psiElement = resolvedReferenceOpt.get();
            if (psiElement instanceof PyFunction pyFunction) {
                PyParameter[] parameters = pyFunction.getParameterList().getParameters();
                for (PyParameter parameter : parameters) {
                    PyNamedParameter parameterName = parameter.getAsNamed();
                    if (parameterName != null) {
                        if (!parameterName.isSelf()) {
                            results.add(new VariableDto(parameter, parameterName.getRepr(false), ReservedVariableScope.KeywordStatement));
                        }
                    }
                }
            }
        }
        return results;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        this.parameters = null;
        this.arguments = null;
        this.invokable = null;
        this.variable = null;
        this.availableKeywordParameters = null;
    }
}
