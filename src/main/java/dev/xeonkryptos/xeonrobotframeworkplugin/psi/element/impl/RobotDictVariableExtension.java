package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotDictVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotDictVariableStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RobotDictVariableExtension extends RobotStubPsiElementBase<RobotDictVariableStub, RobotDictVariable> implements RobotDictVariable {

    public RobotDictVariableExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotDictVariableExtension(RobotDictVariableStub stub, IStubElementType<RobotDictVariableStub, RobotDictVariable> nodeType) {
        super(stub, nodeType);
    }

    @Nullable
    @Override
    public String getVariableName() {
        return RobotPsiUtil.getVariableName(this);
    }
}
