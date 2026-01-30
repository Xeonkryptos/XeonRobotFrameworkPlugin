package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotBddStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExecutableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatementExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatementId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotUserKeywordStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotUserKeywordInputArgumentCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    public Collection<DefinedVariable> getGlobalVariables() {
        if (globalVariables == null) {
            Set<DefinedVariable> variables = new HashSet<>();
            Set<RobotUserKeywordStatement> userKeywords = new HashSet<>();
            acceptChildren(new RobotVisitor() {
                @Override
                public void visitBddStatement(@NotNull RobotBddStatement o) {
                    o.acceptChildren(this);
                }

                @Override
                public void visitExecutableStatement(@NotNull RobotExecutableStatement o) {
                    o.acceptChildren(this);
                }

                @Override
                public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
                    VariableScope scope = o.getScope();
                    if (scope == VariableScope.Global) {
                        variables.add(o);
                    }
                }

                @Override
                public void visitKeywordCall(@NotNull RobotKeywordCall o) {
                    PsiElement calledKeyword = o.getKeywordCallName().getReference().resolve();
                    if (calledKeyword instanceof RobotUserKeywordStatement userKeywordStatement) {
                        userKeywords.add(userKeywordStatement);
                    }
                }
            });

            userKeywords.stream().map(RobotUserKeywordStatementExpression::getGlobalVariables).flatMap(Collection::stream).forEach(variables::add);
            globalVariables = variables;
        }
        return globalVariables;
    }

    @Nullable
    @Override
    public FoldingDescriptor[] fold(@NotNull Document document) {
        if (!RobotFoldingComputationUtil.isFoldingUseful(this, document)) {
            return null;
        }
        var foldingDescriptor = RobotFoldingComputationUtil.computeFoldingDescriptorForContainer(this, getUserKeywordStatementId(), document);
        return foldingDescriptor != null ? new FoldingDescriptor[] { foldingDescriptor } : null;
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
