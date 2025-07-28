package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotTestCaseStatementImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.TestCaseNameIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RobotTestCaseStatementStubElement extends IStubElementType<RobotTestCaseStatementStub, RobotTestCaseStatement> {

    public RobotTestCaseStatementStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    public static RobotTestCaseStatementStubElement create(@NotNull @NonNls String debugName) {
        return new RobotTestCaseStatementStubElement(debugName);
    }

    @Override
    public RobotTestCaseStatement createPsi(@NotNull RobotTestCaseStatementStub stub) {
        return new RobotTestCaseStatementImpl(stub, this);
    }

    @NotNull
    @Override
    public RobotTestCaseStatementStub createStub(@NotNull RobotTestCaseStatement psi, StubElement<? extends PsiElement> parentStub) {
        return new RobotTestCaseStatementStubImpl(parentStub, psi.getName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof RobotTestCaseStatement;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull RobotTestCaseStatementStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @NotNull
    @Override
    public RobotTestCaseStatementStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new RobotTestCaseStatementStubImpl(parentStub, dataStream.readNameString());
    }

    @Override
    public void indexStub(@NotNull RobotTestCaseStatementStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        sink.occurrence(TestCaseNameIndex.KEY, name.toLowerCase());
        sink.occurrence(TestCaseNameIndex.KEY, name);
    }
}
