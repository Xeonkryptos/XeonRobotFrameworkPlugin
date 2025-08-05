package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTestCaseStatementStub;
import org.jetbrains.annotations.NotNull;

public abstract class RobotTestCaseExtension extends RobotStubPsiElementBase<RobotTestCaseStatementStub, RobotTestCaseStatement> implements RobotTestCaseStatement {

    public RobotTestCaseExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotTestCaseExtension(RobotTestCaseStatementStub stub, IStubElementType<RobotTestCaseStatementStub, RobotTestCaseStatement> nodeType) {
        super(stub, nodeType);
    }

    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        return this;
    }
}
