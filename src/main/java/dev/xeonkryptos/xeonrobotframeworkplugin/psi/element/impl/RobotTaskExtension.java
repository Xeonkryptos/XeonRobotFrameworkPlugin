package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.folding.RobotFoldingComputationUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubPsiElementBase;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotTaskStatementStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;

public abstract class RobotTaskExtension extends RobotStubPsiElementBase<RobotTaskStatementStub, RobotTaskStatement> implements RobotTaskStatement {

    public RobotTaskExtension(@NotNull ASTNode node) {
        super(node);
    }

    public RobotTaskExtension(RobotTaskStatementStub stub, IStubElementType<RobotTaskStatementStub, RobotTaskStatement> nodeType) {
        super(stub, nodeType);
    }

    @Nullable
    @Override
    public FoldingDescriptor fold(@NotNull Document document) {
        return RobotFoldingComputationUtil.computeFoldingDescriptorForIdBasedContainerRepresentation(this, getTaskId(), document);
    }

    @NotNull
    @Override
    public abstract String getName();

    @NotNull
    @Override
    public abstract Icon getIcon(int flags);

    @Override
    public PsiElement setName(@NotNull String newName) throws IncorrectOperationException {
        RobotTaskId newTaskId = RobotElementGenerator.getInstance(getProject()).createNewTaskId(newName);
        if (newTaskId != null) {
            getTaskId().replace(newTaskId);
        }
        return this;
    }
}
