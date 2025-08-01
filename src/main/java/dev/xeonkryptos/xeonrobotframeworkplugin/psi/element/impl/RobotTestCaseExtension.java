package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
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
}
