package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTaskStatementStub;
import org.jetbrains.annotations.NotNull;

public abstract class RobotTaskExtension extends RobotStubPsiElementBase<RobotTaskStatementStub, RobotTaskStatement> implements RobotTaskStatement {

    public RobotTaskExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotTaskExtension(RobotTaskStatementStub stub, IStubElementType<RobotTaskStatementStub, RobotTaskStatement> nodeType) {
        super(stub, nodeType);
    }
}
