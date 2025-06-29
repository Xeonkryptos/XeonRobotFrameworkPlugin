package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSingleVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotSingleVariableStatementStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RobotSingleVariableStatementExtension extends RobotStubPsiElementBase<RobotSingleVariableStatementStub, RobotSingleVariableStatement>
        implements RobotSingleVariableStatement {

    public RobotSingleVariableStatementExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotSingleVariableStatementExtension(RobotSingleVariableStatementStub stub,
                                                 IStubElementType<RobotSingleVariableStatementStub, RobotSingleVariableStatement> nodeType) {
        super(stub, nodeType);
    }

    @Nullable
    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement getNameIdentifier() {
        return getVariable();
    }
}
