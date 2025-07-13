package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotScalarVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotScalarVariableStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import org.jetbrains.annotations.NotNull;

public abstract class RobotScalarVariableExtension extends RobotStubPsiElementBase<RobotScalarVariableStub, RobotScalarVariable> implements RobotScalarVariable {

    public RobotScalarVariableExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotScalarVariableExtension(RobotScalarVariableStub stub, IStubElementType<RobotScalarVariableStub, RobotScalarVariable> nodeType) {
        super(stub, nodeType);
    }
}
