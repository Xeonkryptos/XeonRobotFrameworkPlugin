package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.RobotArgumentReference;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.element.PositionalArgumentStub;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;

public class PositionalArgumentImpl extends RobotStubPsiElementBase<PositionalArgumentStub, PositionalArgument> implements PositionalArgument {

    public PositionalArgumentImpl(@NotNull ASTNode node) {
        super(node);
    }

    public PositionalArgumentImpl(@NotNull PositionalArgumentStub stub, @NotNull IStubElementType<PositionalArgumentStub, PositionalArgument> nodeType) {
        super(stub, nodeType);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
    }

    @NotNull
    @Override
    public String getPresentableText() {
        PositionalArgumentStub stub = getStub();
        if (stub != null) {
            return stub.getValue();
        }
        return super.getPresentableText();
    }

    @Override
    public String getContent() {
        PositionalArgumentStub stub = getStub();
        if (stub != null) {
            return stub.getValue();
        }
        return super.getPresentableText();
    }

    @Override
    public boolean isImportArgument() {
        PositionalArgumentStub stub = getStub();
        if (stub != null) {
            return stub.isImportArgument();
        }
        return getParent() instanceof Import;
    }

    @Override
    public PsiReference getReference() {
        return new RobotArgumentReference(this);
    }
}
