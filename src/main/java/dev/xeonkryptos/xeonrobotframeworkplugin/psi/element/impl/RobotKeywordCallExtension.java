package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotKeywordCallStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.KeywordStatementStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RobotKeywordCallExtension extends RobotStubPsiElementBase<RobotKeywordCallStub, RobotKeywordCall> implements RobotKeywordCall {

    public RobotKeywordCallExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotKeywordCallExtension(final RobotKeywordCallStub stub, final IStubElementType<RobotKeywordCallStub, RobotKeywordCall> nodeType) {
        super(stub, nodeType);
    }

    @Nullable
    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement getNameIdentifier() {
        return getKeywordCallId();
    }
}
