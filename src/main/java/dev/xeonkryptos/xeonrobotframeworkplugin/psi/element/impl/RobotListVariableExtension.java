package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotListVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotListVariableStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import org.jetbrains.annotations.NotNull;

public abstract class RobotListVariableExtension extends RobotStubPsiElementBase<RobotListVariableStub, RobotListVariable> implements RobotListVariable {

    public RobotListVariableExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotListVariableExtension(RobotListVariableStub stub, IStubElementType<RobotListVariableStub, RobotListVariable> nodeType) {
        super(stub, nodeType);
    }
}
