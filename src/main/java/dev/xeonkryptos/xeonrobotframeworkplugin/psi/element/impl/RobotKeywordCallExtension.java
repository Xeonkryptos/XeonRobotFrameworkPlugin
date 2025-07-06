package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyNamedParameter;
import com.jetbrains.python.psi.PyParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.MyLogger;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ParameterDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotKeywordCallStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.QualifiedNameBuilder;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotKeywordCallArgumentsCollector;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.PythonInspector;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class RobotKeywordCallExtension extends RobotStubPsiElementBase<RobotKeywordCallStub, RobotKeywordCall> implements RobotKeywordCall {

    private Collection<DefinedParameter> availableKeywordParameters;

    private boolean liveInspectionEnabledLastTime;


    public RobotKeywordCallExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotKeywordCallExtension(final RobotKeywordCallStub stub, final IStubElementType<RobotKeywordCallStub, RobotKeywordCall> nodeType) {
        super(stub, nodeType);
    }

    @Override
    public PsiElement getNameIdentifier() {
        return getKeywordCallId();
    }

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

    @SuppressWarnings("UnstableApiUsage")
    private Collection<DefinedParameter> collectKeywordParameters() {
        Set<DefinedParameter> results = new LinkedHashSet<>();

        PsiElement psiElement = getKeywordCallId().getReference().resolve();
        if (psiElement != null) {
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
                RobotUserKeywordStatement keywordDefinition = (RobotUserKeywordStatement) psiElement;
                Collection<DefinedParameter> parameters = keywordDefinition.getInputParameters();
                results.addAll(parameters);
            }
            return results;
        }
        return null;
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
        Set<String> definedParameters = getParameterList().stream().map(RobotParameter::getName).collect(Collectors.toSet());
        Collection<String> requiredParameterNames = getAvailableParameters().stream()
                                                                            .filter(param -> !param.hasDefaultValue())
                                                                            .map(DefinedParameter::getLookup)
                                                                            .collect(Collectors.toSet());
        definedParameters.retainAll(requiredParameterNames);
        int missingParameterCount = requiredParameterNames.size() - definedParameters.size() - getPositionalArgumentList().size();
        return missingParameterCount <= 0;
    }

    @Override
    public Collection<RobotArgument> getAllCallArguments() {
        RobotKeywordCallArgumentsCollector callArgumentsCollector = new RobotKeywordCallArgumentsCollector();
        acceptChildren(callArgumentsCollector);
        return callArgumentsCollector.getArguments();
    }

    @NotNull
    @Override
    public String getQualifiedName() {
        return QualifiedNameBuilder.computeQualifiedName(this);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        availableKeywordParameters = null;
    }

    @NotNull
    @Override
    public Icon getIcon(int flags) {
        return RobotIcons.FUNCTION;
    }
}
