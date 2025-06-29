package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotUserKeywordStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RobotUserKeywordExtension extends RobotStubPsiElementBase<RobotUserKeywordStub, RobotUserKeywordStatement>
        implements RobotUserKeywordStatement {

    public RobotUserKeywordExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotUserKeywordExtension(final RobotUserKeywordStub stub, final IStubElementType<RobotUserKeywordStub, RobotUserKeywordStatement> nodeType) {
        super(stub, nodeType);
    }

    @Override
    public PsiElement setName(@NotNull String s) throws IncorrectOperationException {
        return null;
    }

    @Override
    public @Nullable PsiElement getNameIdentifier() {
        return getUserKeywordStatementId();
    }
}
