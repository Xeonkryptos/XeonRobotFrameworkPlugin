package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatementExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatementId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotUserKeywordStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.KeywordCallNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableDefinitionNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotUserKeywordInputArgumentCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class RobotUserKeywordExtension extends RobotStubPsiElementBase<RobotUserKeywordStub, RobotUserKeywordStatement> implements RobotUserKeywordStatement {

    private Collection<DefinedParameter> inputParameters;
    private Collection<DefinedVariable> globalVariables;

    public RobotUserKeywordExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotUserKeywordExtension(final RobotUserKeywordStub stub, final IStubElementType<RobotUserKeywordStub, RobotUserKeywordStatement> nodeType) {
        super(stub, nodeType);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        inputParameters = null;
        globalVariables = null;
    }

    @Override
    public Collection<DefinedParameter> getInputParameters() {
        if (inputParameters == null) {
            Optional<RobotLocalArgumentsSetting> argumentsSetting = getLocalArgumentsSettingList().stream().findFirst();
            if (argumentsSetting.isPresent()) {
                RobotLocalArgumentsSetting robotLocalSetting = argumentsSetting.get();
                RobotUserKeywordInputArgumentCollector inputArgumentCollector = new RobotUserKeywordInputArgumentCollector();
                robotLocalSetting.acceptChildren(inputArgumentCollector);
                inputParameters = inputArgumentCollector.getInputArguments();
            } else {
                inputParameters = List.of();
            }
        }
        return inputParameters;
    }

    @Override
    public Collection<DefinedVariable> getDynamicGlobalVariables() {
        if (globalVariables == null) {
            Project project = getProject();
            GlobalSearchScope fileScope = GlobalSearchScope.fileScope(getContainingFile().getOriginalFile());
            int textOffset = getTextOffset();
            Set<DefinedVariable> variables = VariableDefinitionNameIndex.getInstance()
                                                                        .getVariableDefinitions(project, fileScope)
                                                                        .stream()
                                                                        .filter(variableDefinition -> variableDefinition.getScope() == VariableScope.Global
                                                                                                      && Optional.ofNullable(variableDefinition.getParent()).map(PsiElement::getTextOffset).orElse(-1)
                                                                                                         == textOffset)
                                                                        .collect(Collectors.toCollection(LinkedHashSet::new));

            KeywordCallNameIndex.getInstance()
                                .getKeywordCalls(project, fileScope)
                                .stream()
                                .filter(keywordCall -> keywordCall.getParent() == this)
                                .map(keywordCall -> keywordCall.getKeywordCallName().getReference().resolve())
                                .filter(resolvedElement -> resolvedElement instanceof RobotUserKeywordStatement)
                                .map(resolvedElement -> (RobotUserKeywordStatement) resolvedElement)
                                .map(RobotUserKeywordStatementExpression::getDynamicGlobalVariables)
                                .forEach(variables::addAll);

            globalVariables = variables;
        }
        return globalVariables;
    }

    @Override
    public @NotNull FoldingDescriptor @NotNull [] fold(@NotNull Document document, boolean quick) {
        if (!RobotFoldingComputationUtil.isFoldingUseful(this, document)) {
            return FoldingDescriptor.EMPTY_ARRAY;
        }
        var foldingDescriptor = RobotFoldingComputationUtil.computeFoldingDescriptorForContainer(this, getUserKeywordStatementId(), document);
        return foldingDescriptor != null ? new FoldingDescriptor[] { foldingDescriptor } : FoldingDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public abstract String getName();

    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        RobotUserKeywordStatementId newUserKeywordStatementId = RobotElementGenerator.getInstance(getProject()).createNewUserKeywordStatementId(newName);
        if (newUserKeywordStatementId != null) {
            getNameIdentifier().replace(newUserKeywordStatementId);
        }
        return this;
    }
}
