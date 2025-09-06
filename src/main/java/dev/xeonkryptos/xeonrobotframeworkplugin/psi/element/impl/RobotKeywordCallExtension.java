package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.ParameterizedCachedValue;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyNamedParameter;
import com.jetbrains.python.psi.PyParameter;
import com.jetbrains.python.psi.PyParameterList;
import com.jetbrains.python.psi.PySingleStarParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ParameterDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotKeywordCallStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.QualifiedNameBuilder;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RecursiveRobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotKeywordCallArgumentsCollector;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;
import java.text.Collator;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class RobotKeywordCallExtension extends RobotStubPsiElementBase<RobotKeywordCallStub, RobotKeywordCall> implements RobotKeywordCall {

    private static final Key<ParameterizedCachedValue<Collection<DefinedParameter>, PsiElement>> AVAILABLE_KEYWORD_PARAMETERS_KEY = Key.create(
            "AVAILABLE_KEYWORD_PARAMETERS_KEY");
    private static final Key<CachedValue<OptionalInt>> START_OF_KEYWORDS_ONLY_INDEX_KEY = Key.create("START_OF_KEYWORDS_ONLY_INDEX_KEY");

    private Collection<RobotArgument> allCallArguments;

    public RobotKeywordCallExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotKeywordCallExtension(final RobotKeywordCallStub stub, final IStubElementType<RobotKeywordCallStub, RobotKeywordCall> nodeType) {
        super(stub, nodeType);
    }

    @Override
    public Collection<DefinedParameter> getAvailableParameters() {
        PsiElement psiElement = getKeywordCallName().getReference().resolve();
        if (psiElement != null) {
            CachedValuesManager cachedValuesManager = CachedValuesManager.getManager(getProject());
            return cachedValuesManager.getParameterizedCachedValue(this, AVAILABLE_KEYWORD_PARAMETERS_KEY, element -> {
                if (element instanceof PyFunction pyFunction) {
                    Set<DefinedParameter> results = new LinkedHashSet<>();
                    PyParameter[] pyParameters = pyFunction.getParameterList().getParameters();
                    inspectPythonFunctionStatically(pyParameters, results);
                    return Result.create(results, pyFunction, this);
                } else {
                    RobotUserKeywordStatement keywordDefinition = (RobotUserKeywordStatement) element;
                    Collection<DefinedParameter> parameters = keywordDefinition.getInputParameters();
                    return Result.create(parameters, keywordDefinition, this);
                }
            }, false, psiElement);
        }
        return List.of();
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
    public Collection<String> computeMissingRequiredParameters() {
        Set<String> definedParameters = getParameterList().stream().map(RobotParameter::getParameterName).collect(Collectors.toCollection(LinkedHashSet::new));
        Collection<DefinedParameter> requiredParameterNames = getAvailableParameters().stream()
                                                                                      .filter(param -> !param.hasDefaultValue() && !param.isKeywordContainer())
                                                                                      .collect(Collectors.toCollection(LinkedHashSet::new));
        removeMatchingParameters(definedParameters, getAvailableParameters());
        int missingParameterCount = requiredParameterNames.size() - getPositionalArgumentList().size();
        if (missingParameterCount <= 0) {
            return List.of();
        }
        return requiredParameterNames.stream().map(DefinedParameter::getLookup).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Collection<DefinedParameter> computeMissingParameters() {
        Set<String> definedParameters = getParameterList().stream().map(RobotParameter::getParameterName).collect(Collectors.toCollection(LinkedHashSet::new));
        Collection<DefinedParameter> availableParameters = new LinkedHashSet<>(getAvailableParameters());
        removeMatchingParameters(definedParameters, getAvailableParameters());
        return availableParameters;
    }

    private void removeMatchingParameters(Set<String> definedParameters, Collection<? extends DefinedParameter> availableParameters) {
        RobotOptionsProvider robotOptionsProvider = RobotOptionsProvider.getInstance(getProject());
        Collator parameterNameCollator = robotOptionsProvider.getParameterNameCollator();

        Iterator<String> definedParamsIterator = definedParameters.iterator();
        while (definedParamsIterator.hasNext()) {
            String definedParamName = definedParamsIterator.next();
            Iterator<? extends DefinedParameter> availableParamsIterator = availableParameters.iterator();
            while (availableParamsIterator.hasNext()) {
                DefinedParameter availableParameter = availableParamsIterator.next();
                String availableParameterName = availableParameter.getLookup();
                if (parameterNameCollator.equals(definedParamName, availableParameterName)) {
                    definedParamsIterator.remove();
                    availableParamsIterator.remove();
                    break;
                }
            }
        }
    }

    @Override
    public Collection<RobotArgument> getAllCallArguments() {
        if (allCallArguments == null) {
            RobotKeywordCallArgumentsCollector callArgumentsCollector = new RobotKeywordCallArgumentsCollector();
            acceptChildren(callArgumentsCollector);
            allCallArguments = callArgumentsCollector.getArguments();
        }
        return allCallArguments;
    }

    @Override
    public OptionalInt getStartOfKeywordsOnlyIndex() {
        PsiElement reference = getKeywordCallName().getReference().resolve();
        if (reference instanceof RobotUserKeywordStatement userKeywordStatement) {
            return CachedValuesManager.getCachedValue(userKeywordStatement, START_OF_KEYWORDS_ONLY_INDEX_KEY, () -> {
                OptionalInt startOfKeywordsOnlyIndexOpt = computeKeywordsOnlyStartIndexFor(userKeywordStatement);
                return Result.createSingleDependency(startOfKeywordsOnlyIndexOpt, userKeywordStatement);
            });
        } else if (reference instanceof PyFunction pyFunction) {
            return CachedValuesManager.getCachedValue(reference, START_OF_KEYWORDS_ONLY_INDEX_KEY, () -> {
                OptionalInt startOfKeywordsOnlyIndexOpt = computeKeywordsOnlyStartIndexFor(pyFunction);
                return Result.createSingleDependency(startOfKeywordsOnlyIndexOpt, pyFunction);
            });
        }
        return OptionalInt.empty();
    }

    @NotNull
    private static OptionalInt computeKeywordsOnlyStartIndexFor(RobotUserKeywordStatement userKeywordStatement) {
        Integer startOfKeywordsOnlyIndex = null;
        List<RobotLocalArgumentsSetting> argumentsSettings = userKeywordStatement.getLocalArgumentsSettingList();
        if (!argumentsSettings.isEmpty()) {
            RobotLocalArgumentsSetting argumentsSetting = argumentsSettings.getFirst();
            RobotUserKeywordStatementKeywordsOnlyStartIndexFinder visitor = new RobotUserKeywordStatementKeywordsOnlyStartIndexFinder();
            argumentsSetting.acceptChildren(visitor);
            if (visitor.keywordsOnlyIndex != -1) {
                startOfKeywordsOnlyIndex = visitor.keywordsOnlyIndex;
            }
        }
        return startOfKeywordsOnlyIndex != null ? OptionalInt.of(startOfKeywordsOnlyIndex) : OptionalInt.empty();
    }

    @NotNull
    @SuppressWarnings("UnstableApiUsage")
    private static OptionalInt computeKeywordsOnlyStartIndexFor(PyFunction pyFunction) {
        PyParameterList parameterList = pyFunction.getParameterList();

        Integer startOfKeywordsOnlyIndex = null;
        int parameterIndexCorrection = 0;
        PyParameter[] parameters = parameterList.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            PyParameter parameter = parameters[i];
            if (parameter.isSelf()) {
                parameterIndexCorrection++;
            } else {
                String parameterText = parameter.getText();
                if (PySingleStarParameter.TEXT.equals(parameterText)) {
                    startOfKeywordsOnlyIndex = i - parameterIndexCorrection;
                    break;
                }
            }
        }
        return startOfKeywordsOnlyIndex != null ? OptionalInt.of(startOfKeywordsOnlyIndex) : OptionalInt.empty();
    }

    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        RobotKeywordCallName newKeywordCallName = RobotElementGenerator.getInstance(getProject()).createNewKeywordCallName(newName);
        if (newKeywordCallName != null) {
            getKeywordCallName().replace(newKeywordCallName);
        }
        return this;
    }

    @NotNull
    @Override
    public String getQualifiedName() {
        return QualifiedNameBuilder.computeQualifiedName(this);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        allCallArguments = null;
    }

    @NotNull
    @Override
    public Icon getIcon(int flags) {
        return RobotIcons.FUNCTION;
    }

    private static class RobotUserKeywordStatementKeywordsOnlyStartIndexFinder extends RecursiveRobotVisitor {

        private int currentIndex = -1;
        private int keywordsOnlyIndex = -1;

        @Override
        public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
            super.visitVariableDefinition(o);
            ++currentIndex;

            String parameterName = o.getName();
            if (parameterName != null && parameterName.isEmpty()) {
                keywordsOnlyIndex = currentIndex;
            }
        }
    }
}
