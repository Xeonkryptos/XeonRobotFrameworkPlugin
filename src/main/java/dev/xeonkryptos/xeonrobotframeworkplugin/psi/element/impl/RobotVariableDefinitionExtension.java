package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiImplUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotQualifiedNameOwner;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotVariableDefinitionStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariableScope;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.VariableNameUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class RobotVariableDefinitionExtension extends RobotStubPsiElementBase<RobotVariableDefinitionStub, RobotVariableDefinition>
        implements RobotVariableDefinition {

    private Set<String> variableNameVariants;

    public RobotVariableDefinitionExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotVariableDefinitionExtension(RobotVariableDefinitionStub stub, IStubElementType<RobotVariableDefinitionStub, RobotVariableDefinition> nodeType) {
        super(stub, nodeType);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        variableNameVariants = null;
    }

    @Override
    public boolean isInScope(@NotNull PsiElement element) {
        RobotVariableStatement variableStatement = PsiTreeUtil.getParentOfType(this, RobotVariableStatement.class);
        if (variableStatement != null) {
            return ReservedVariableScope.TestCase.isInScope(this, element);
        }
        RobotVariablesSection variablesSection = PsiTreeUtil.getParentOfType(this, RobotVariablesSection.class);
        if (variablesSection == null || variablesSection.getContainingFile().getFileType() != RobotResourceFileType.getInstance()) {
            return getContainingFile() == element.getContainingFile();
        }
        RobotQualifiedNameOwner statementParent = PsiTreeUtil.getParentOfType(this,
                                                                              RobotTestCaseStatement.class,
                                                                              RobotTaskStatement.class,
                                                                              RobotUserKeywordStatement.class);
        if (statementParent != null) {
            RobotQualifiedNameOwner statementParentOfVariable = PsiTreeUtil.getParentOfType(element,
                                                                                            RobotTestCaseStatement.class,
                                                                                            RobotTaskStatement.class,
                                                                                            RobotUserKeywordStatement.class);
            return statementParent == statementParentOfVariable;
        }
        return true;
    }

    @Override
    public boolean matches(@Nullable String text) {
        if (text == null) {
            return false;
        }
        if (variableNameVariants == null) {
            String variableName = getName();
            variableNameVariants = VariableNameUtil.INSTANCE.computeVariableNameVariants(variableName);
        }
        return VariableNameUtil.INSTANCE.matchesVariableName(text, variableNameVariants);
    }

    @Override
    public int getTextOffset() {
        return super.getTextOffset() + 2; // to skip the ${, @%, &% or %% at the beginning of the variable
    }

    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        RobotVariableBodyId newVariableBodyId = RobotElementGenerator.getInstance(getProject()).createNewVariableBodyId(newName);
        RobotVariableBodyId variableBodyId = RobotPsiImplUtil.getVariableBodyId(getVariable());
        if (variableBodyId != null && newVariableBodyId != null) {
            variableBodyId.replace(newVariableBodyId);
        }
        return this;
    }

    @Override
    public String getLookup() {
        return getText();
    }

    @Override
    public String[] getLookupWords() {
        return new String[] { getName(), getText() };
    }

    @Override
    public PsiElement reference() {
        return this;
    }
}
