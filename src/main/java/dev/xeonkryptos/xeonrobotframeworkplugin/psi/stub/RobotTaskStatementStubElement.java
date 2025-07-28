package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotTaskStatementImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.TaskNameIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RobotTaskStatementStubElement extends IStubElementType<RobotTaskStatementStub, RobotTaskStatement> {

    public RobotTaskStatementStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    public static RobotTaskStatementStubElement create(@NotNull @NonNls String debugName) {
        return new RobotTaskStatementStubElement(debugName);
    }

    @Override
    public RobotTaskStatement createPsi(@NotNull RobotTaskStatementStub stub) {
        return new RobotTaskStatementImpl(stub, this);
    }

    @NotNull
    @Override
    public RobotTaskStatementStub createStub(@NotNull RobotTaskStatement psi, StubElement<? extends PsiElement> parentStub) {
        return new RobotTaskStatementStubImpl(parentStub, psi.getName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof RobotTaskStatement;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull RobotTaskStatementStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @NotNull
    @Override
    public RobotTaskStatementStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new RobotTaskStatementStubImpl(parentStub, dataStream.readNameString());
    }

    @Override
    public void indexStub(@NotNull RobotTaskStatementStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        sink.occurrence(TaskNameIndex.KEY, name.toLowerCase());
        sink.occurrence(TaskNameIndex.KEY, name);
    }
}
