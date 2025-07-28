package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.icons.RobotIcons;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTaskStatementStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.QualifiedNameBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public abstract class RobotTaskExtension extends RobotStubPsiElementBase<RobotTaskStatementStub, RobotTaskStatement> implements RobotTaskStatement {

    public RobotTaskExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotTaskExtension(RobotTaskStatementStub stub, IStubElementType<RobotTaskStatementStub, RobotTaskStatement> nodeType) {
        super(stub, nodeType);
    }

    @NotNull
    @Override
    public String getName() {
        return getNameIdentifier().getName();
    }

    @NotNull
    @Override
    public Icon getIcon(int flags) {
        return RobotIcons.JUNIT;
    }

    @NotNull
    @Override
    public String getQualifiedName() {
        return QualifiedNameBuilder.computeQualifiedName(this);
    }
}
