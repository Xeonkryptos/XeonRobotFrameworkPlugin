package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyNamedParameter;
import com.jetbrains.python.psi.PyParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.MyLogger;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ParameterDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.KeywordStatementStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.PatternUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.QualifiedNameBuilder;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.PythonInspector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    @NotNull
    @Override
    public final KeywordInvokable getInvokable() {
        KeywordInvokable result = invokable;
        if (invokable == null || !invokable.isValid()) {
            result = PsiTreeUtil.getRequiredChildOfType(this, KeywordInvokable.class);
            this.invokable = result;
        }
        return result;
    }

    @NotNull
    @Override
    public List<Argument> getArguments() {
        List<Argument> arguments = this.arguments;
        if (arguments == null || arguments.stream().anyMatch(element -> !element.isValid())) {
            arguments = PsiTreeUtil.getChildrenOfTypeAsList(this, Argument.class);
            this.arguments = arguments;
        }
        return arguments;
    }

    @NotNull
    @Override
    public List<Parameter> getParameters() {
        List<Parameter> results = parameters;
        if (parameters == null || results.stream().anyMatch(element -> !element.isValid())) {
            results = PsiTreeUtil.getChildrenOfTypeAsList(this, Parameter.class);
            parameters = results;
        }
        return results;
    }

    @NotNull
    @Override
    public final List<PositionalArgument> getPositionalArguments() {
        List<PositionalArgument> results = positionalArguments;
        if (positionalArguments == null || results.stream().anyMatch(element -> !element.isValid())) {
            results = PsiTreeUtil.getChildrenOfTypeAsList(this, PositionalArgument.class);
            positionalArguments = results;
        }
        return results;
    }

    @NotNull
    @Override
    public Collection<DefinedParameter> getAvailableParameters() {
        Collection<DefinedParameter> localDefinedParameters = availableKeywordParameters;
        boolean pythonLiveInspection = RobotOptionsProvider.getInstance(getProject()).pythonLiveInspection();
        if (availableKeywordParameters == null || liveInspectionEnabledLastTime != pythonLiveInspection || localDefinedParameters.stream()
                                                                                                                                 .map(DefinedParameter::reference)
                                                                                                                                 .anyMatch(element -> !element.isValid())) {
            localDefinedParameters = collectKeywordParameters();
            availableKeywordParameters = localDefinedParameters;
            liveInspectionEnabledLastTime = pythonLiveInspection;
        }
        return localDefinedParameters != null ? localDefinedParameters : Collections.emptySet();
    }

    @Nullable
    @Override
    public final DefinedVariable getGlobalVariable() {
        DefinedVariable result = variable;
        if (result == null || !result.reference().isValid()) {
            KeywordInvokable invokable = getInvokable();
            String text = invokable.getName();
            if (PatternUtil.isVariableSettingKeyword(text)) {
                List<PositionalArgument> positionalArguments = getPositionalArguments();
                if (!positionalArguments.isEmpty()) {
                    PositionalArgument variable = positionalArguments.getFirst();
                    // already formatted ${X}
                    result = new VariableDto(variable, variable.getContent(), null);
                }
            }
            variable = result;
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
                    try {
                        PythonInspector.PythonInspectorParameter[] parameters = PythonInspector.inspectPythonFunction(pyFunction);
                        Collection<DefinedParameter> definedParameters = PythonInspector.convertPyParameters(parameters, pyParameters, containingClass != null);
                        results.addAll(definedParameters);
                    } catch (RuntimeException e) {
                        MyLogger.logger.warn("Error while inspecting Python function: " + pyFunction.getName() + " of keyword " + getName()
                                             + ". Falling back to static analysis.", e);
                        inspectPythonFunctionStatically(pyParameters, results);
                    }
                } else {
                    inspectPythonFunctionStatically(pyParameters, results);
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

    @SuppressWarnings("UnstableApiUsage")
    private static void inspectPythonFunctionStatically(PyParameter[] pyParameters, Set<DefinedParameter> results) {
        for (PyParameter parameter : pyParameters) {
            PyNamedParameter parameterName = parameter.getAsNamed();
            if (parameterName != null && !parameterName.isSelf() && !parameterName.isPositionalContainer() && !parameterName.isKeywordContainer()) {
                String defaultValueText = parameter.getDefaultValueText();
                results.add(new ParameterDto(parameter, parameterName.getRepr(false), defaultValueText));
            }
            if (parameterName != null && parameterName.isKeywordContainer()) {
                results.add(new ParameterDto(parameter, parameterName.getRepr(false), null, true));
            }
        }
    }

    @Override
    public boolean allRequiredParametersArePresent() {
        Set<String> definedParameters = getParameters().stream().map(Parameter::getParameterName).collect(Collectors.toSet());
        Collection<String> requiredParameterNames = getAvailableParameters().stream()
                                                                            .filter(param -> !param.hasDefaultValue())
                                                                            .map(DefinedParameter::getLookup)
                                                                            .collect(Collectors.toSet());
        definedParameters.retainAll(requiredParameterNames);
        int missingParameterCount = requiredParameterNames.size() - definedParameters.size() - getArguments().size();
        return missingParameterCount <= 0;
    }

    @Override
    public @NotNull String getQualifiedName() {
        return QualifiedNameBuilder.computeQualifiedName(this);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        reset();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return RobotIcons.FUNCTION;
    }

    @Override
    public void reset() {
        parameters = null;
        positionalArguments = null;
        arguments = null;
        invokable = null;
        variable = null;
        availableKeywordParameters = null;
    }
}
