package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotKeywordVariableStatementStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RobotKeywordVariableStatementExtension extends RobotStubPsiElementBase<RobotKeywordVariableStatementStub, RobotKeywordVariableStatement>
        implements RobotKeywordVariableStatement {

    public RobotKeywordVariableStatementExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotKeywordVariableStatementExtension(RobotKeywordVariableStatementStub stub,
                                                  IStubElementType<RobotKeywordVariableStatementStub, RobotKeywordVariableStatement> nodeType) {
        super(stub, nodeType);
    }

    @Nullable
    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        return null;
    }
}
