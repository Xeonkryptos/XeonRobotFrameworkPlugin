package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.config.RobotOptionsProvider;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.ParameterDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.VariableDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.element.KeywordStatementStub;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.PatternUtil;
import com.github.jnhyperion.hyperrobotframeworkplugin.util.PythonInspector;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyNamedParameter;
import com.jetbrains.python.psi.PyParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class KeywordStatementImpl extends RobotStubPsiElementBase<KeywordStatementStub, KeywordStatement> implements KeywordStatement {

    private List<Argument> arguments;
    private List<Parameter> parameters;
    private List<PositionalArgument> positionalArguments;
    private DefinedVariable variable;
    private KeywordInvokable invokable;
    private Collection<DefinedParameter> availableKeywordParameters;

    private boolean liveInspectionEnabledLastTime;

    public KeywordStatementImpl(@NotNull ASTNode node) {
        super(node);
    }

    public KeywordStatementImpl(final KeywordStatementStub stub, final IStubElementType<KeywordStatementStub, KeywordStatement> nodeType) {
        super(stub, nodeType);
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

    @NotNull
    @Override
    public List<Parameter> getParameters() {
        List<Parameter> results = this.parameters;
        if (this.parameters == null) {
            results = new ArrayList<>(PsiTreeUtil.getChildrenOfTypeAsList(this, Parameter.class));
            this.parameters = results;
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
        boolean pythonLiveInspection = RobotOptionsProvider.getInstance(getProject()).pythonLiveInspection();
        if (this.availableKeywordParameters == null || liveInspectionEnabledLastTime != pythonLiveInspection) {
            localDefinedParameters = collectKeywordParameters();
            availableKeywordParameters = localDefinedParameters;
            liveInspectionEnabledLastTime = pythonLiveInspection;
        }
        return localDefinedParameters != null ? localDefinedParameters : Collections.emptySet();
    }

    @Nullable
    @Override
    public final DefinedVariable getGlobalVariable() {
        DefinedVariable result = this.variable;
        if (result == null) {
            KeywordInvokable invokable = getInvokable();
            if (invokable != null) {
                String text = invokable.getName();
                if (PatternUtil.isVariableSettingKeyword(text)) {
                    List<PositionalArgument> positionalArguments = getPositionalArguments();
                    if (!positionalArguments.isEmpty()) {
                        PositionalArgument variable = positionalArguments.getFirst();
                        // already formatted ${X}
                        result = new VariableDto(variable, variable.getContent(), null);
                    }
                }
            }
            this.variable = result;
        }
        return result;
    }

    @SuppressWarnings("UnstableApiUsage")
    private Collection<DefinedParameter> collectKeywordParameters() {
        Set<DefinedParameter> results = new LinkedHashSet<>();

        Optional<PsiElement> resolvedReferenceOpt = Optional.ofNullable(PsiTreeUtil.findChildOfType(this, KeywordInvokable.class))
                                                            .map(KeywordInvokable::getReference)
                                                            .map(PsiReference::resolve);
        if (resolvedReferenceOpt.isPresent()) {
            PsiElement psiElement = resolvedReferenceOpt.get();
            if (psiElement instanceof PyFunction pyFunction) {
                RobotOptionsProvider robotOptionsProvider = RobotOptionsProvider.getInstance(pyFunction.getProject());
                PyParameter[] pyParameters = pyFunction.getParameterList().getParameters();
                if (robotOptionsProvider.analyzeViaPythonLiveInspection(pyFunction)) {
                    PyClass containingClass = pyFunction.getContainingClass();
                    PythonInspector.PythonInspectorParameter[] parameters = PythonInspector.inspectPythonFunction(pyFunction);
                    Collection<DefinedParameter> definedParameters = PythonInspector.convertPyParameters(parameters, pyParameters, containingClass != null);
                    results.addAll(definedParameters);
                } else {
                    for (PyParameter parameter : pyParameters) {
                        PyNamedParameter parameterName = parameter.getAsNamed();
                        if (parameterName != null && !parameterName.isSelf() && !parameterName.isPositionalContainer() && !parameterName.isKeywordContainer()) {
                            String defaultValueText = parameter.getDefaultValueText();
                            results.add(new ParameterDto(parameter, parameterName.getRepr(false), defaultValueText));
                        }
                    }
                }
            } else {
                KeywordDefinition keywordDefinition = (KeywordDefinition) psiElement;
                Collection<DefinedParameter> parameters = keywordDefinition.getParameters();
                results.addAll(parameters);
            }
        } else {
            return null;
        }
        return results;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
        this.parameters = null;
        this.positionalArguments = null;
        this.arguments = null;
        this.invokable = null;
        this.variable = null;
        this.availableKeywordParameters = null;
    }
}
