package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotInlineVariableStatementStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RobotInlineVariableStatementExtension extends RobotStubPsiElementBase<RobotInlineVariableStatementStub, RobotInlineVariableStatement>
        implements RobotInlineVariableStatement {

    public RobotInlineVariableStatementExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotInlineVariableStatementExtension(RobotInlineVariableStatementStub stub,
                                                 IStubElementType<RobotInlineVariableStatementStub, RobotInlineVariableStatement> nodeType) {
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
