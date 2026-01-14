package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.IStubElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotImportArgumentStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import org.jetbrains.annotations.NotNull;

public abstract class RobotImportArgumentExtension extends RobotStubPsiElementBase<RobotImportArgumentStub, RobotImportArgument>
        implements RobotImportArgument {

    public RobotImportArgumentExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotImportArgumentExtension(RobotImportArgumentStub stub, IStubElementType<RobotImportArgumentStub, RobotImportArgument> nodeType) {
        super(stub, nodeType);
    }

    @NotNull
    @Override
    public abstract PsiReference getReference();
}
