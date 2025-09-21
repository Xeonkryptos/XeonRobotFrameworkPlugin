package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiImplUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotVariableDefinitionStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.VariableNameUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class RobotVariableDefinitionExtension extends RobotStubPsiElementBase<RobotVariableDefinitionStub, RobotVariableDefinition>
        implements RobotVariableDefinition {

    private Set<String> variableNameVariants;
    private VariableScope scope;

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
        scope = null;
    }

    @Override
    public boolean isInScope(@NotNull PsiElement element) {
        return getScope().isInScope(this, element);
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

    @NotNull
    @Override
    public VariableScope getScope() {
        if (scope == null) {
            RobotVariableDefinitionStub stub = getStub();
            if (stub != null) {
                scope = stub.getScope();
            }
            if (scope == null) {
                scope = RobotPsiImplUtil.getScope(this);
            }
        }
        return scope;
    }

    @Override
    public PsiElement reference() {
        return this;
    }
}
