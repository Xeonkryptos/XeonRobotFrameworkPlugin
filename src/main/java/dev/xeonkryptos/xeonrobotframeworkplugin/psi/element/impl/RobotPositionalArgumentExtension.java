package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotPositionalArgumentStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import org.jetbrains.annotations.NotNull;

public abstract class RobotPositionalArgumentExtension extends RobotStubPsiElementBase<RobotPositionalArgumentStub, RobotPositionalArgument>
        implements RobotPositionalArgument {

    public RobotPositionalArgumentExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotPositionalArgumentExtension(RobotPositionalArgumentStub stub, IStubElementType<RobotPositionalArgumentStub, RobotPositionalArgument> nodeType) {
        super(stub, nodeType);
    }
}
