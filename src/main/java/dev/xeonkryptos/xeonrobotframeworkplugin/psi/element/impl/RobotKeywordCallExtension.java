package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyNamedParameter;
import com.jetbrains.python.psi.PyParameter;
import com.jetbrains.python.psi.PyParameterList;
import com.jetbrains.python.psi.PySingleStarParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ParameterDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotKeywordCallStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.QualifiedNameBuilder;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RecursiveRobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotCallArgumentsCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.text.Collator;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class RobotKeywordCallExtension extends RobotStubPsiElementBase<RobotKeywordCallStub, RobotKeywordCall> implements RobotKeywordCall {

    private static final Key<CachedValue<OptionalInt>> START_OF_KEYWORDS_ONLY_INDEX_KEY = Key.create("START_OF_KEYWORDS_ONLY_INDEX_KEY");

    private Collection<RobotArgument> allCallArguments;
    private Set<String> definedParameterNames;

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
            if (psiElement instanceof PyFunction pyFunction) {
                Set<DefinedParameter> results = new LinkedHashSet<>();
                PyParameter[] pyParameters = pyFunction.getParameterList().getParameters();
                inspectPythonFunctionStatically(pyParameters, results);
                return results;
            }
            RobotUserKeywordStatement keywordDefinition = (RobotUserKeywordStatement) psiElement;
            return keywordDefinition.getInputParameters();
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
        return KeywordParameterEvaluator.computeMissingRequiredParameters(this, this);
    }

    @Override
    public Collection<DefinedParameter> computeMissingParameters() {
        return KeywordParameterEvaluator.computeMissingParameters(this, this);
    }

    @Override
    public Collection<String> getDefinedParameterNames() {
        if (definedParameterNames == null) {
            definedParameterNames = getParameterList().stream().map(RobotParameter::getParameterName).collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return new LinkedHashSet<>(definedParameterNames);
    }

    @Nullable
    @Override
    public PsiElement findParameterReference(String parameterName) {
        RobotOptionsProvider robotOptionsProvider = RobotOptionsProvider.getInstance(getProject());
        Collator parameterNameCollator = robotOptionsProvider.getParameterNameCollator();

        PsiElement reference = getAvailableParameters().stream()
                                                       .filter(param -> parameterNameCollator.equals(parameterName, param.getLookup()) || param.isKeywordContainer())
                                                       .min(Comparator.comparing(DefinedParameter::isKeywordContainer, (kc1, kc2) -> kc1 == kc2 ? 0 : kc1 ? 1 : -1))
                                                       .map(DefinedParameter::reference)
                                                       .orElse(null);
        if (reference == null) {
            // Fall back to PyFunction element. The parameter itself couldn't be found
            RobotKeywordCallName keywordCallName = getKeywordCallName();
            reference = keywordCallName.getReference().resolve();
        }
        return reference;
    }

    @Override
    public Collection<RobotArgument> getAllCallArguments() {
        if (allCallArguments == null) {
            RobotCallArgumentsCollector callArgumentsCollector = new RobotCallArgumentsCollector();
            acceptChildren(callArgumentsCollector);
            allCallArguments = callArgumentsCollector.getArguments();
        }
        return allCallArguments;
    }

    @Override
    public Collection<RobotArgument> getPositionalArguments() {
        return getPositionalArgumentList().stream().map(argument -> (RobotArgument) argument).collect(Collectors.toList());
    }

    @Override
    public OptionalInt getStartOfKeywordsOnlyIndex() {
        PsiElement reference = getKeywordCallName().getReference().resolve();
        if (reference instanceof RobotUserKeywordStatement userKeywordStatement) {
            return CachedValuesManager.getCachedValue(userKeywordStatement, START_OF_KEYWORDS_ONLY_INDEX_KEY, () -> {
                OptionalInt startOfKeywordsOnlyIndexOpt = computeKeywordsOnlyStartIndexFor(userKeywordStatement);
                return Result.createSingleDependency(startOfKeywordsOnlyIndexOpt, PsiModificationTracker.MODIFICATION_COUNT);
            });
        } else if (reference instanceof PyFunction pyFunction) {
            return CachedValuesManager.getCachedValue(reference, START_OF_KEYWORDS_ONLY_INDEX_KEY, () -> {
                OptionalInt startOfKeywordsOnlyIndexOpt = computeKeywordsOnlyStartIndexFor(pyFunction);
                return Result.createSingleDependency(startOfKeywordsOnlyIndexOpt, PsiModificationTracker.MODIFICATION_COUNT);
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

    @Nullable
    @Override
    public FoldingDescriptor[] fold(@NotNull Document document) {
        if (!RobotFoldingComputationUtil.isFoldingUseful(this, document)) {
            return null;
        }
        var foldingDescriptors = RobotFoldingComputationUtil.computeFoldingDescriptorsForListing(getNode(), "KeywordCallArgumentsListFolding", getKeywordCallName(), getAllCallArguments(), document);
        return !foldingDescriptors.isEmpty() ? foldingDescriptors.toArray(FoldingDescriptor.EMPTY_ARRAY) : null;
    }

    @NotNull
    @Override
    public abstract String getName();

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
        definedParameterNames = null;
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
            if (parameterName == null || parameterName.isBlank()) {
                keywordsOnlyIndex = currentIndex;
            }
        }
    }
}
