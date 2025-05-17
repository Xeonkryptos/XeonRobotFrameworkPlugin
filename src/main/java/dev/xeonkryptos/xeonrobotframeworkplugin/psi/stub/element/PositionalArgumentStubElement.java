package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.PositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.PositionalArgumentImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.PositionalArgumentImportIndex;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PositionalArgumentStubElement extends IStubElementType<PositionalArgumentStub, PositionalArgument> {

    public PositionalArgumentStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    @Override
    public PositionalArgument createPsi(@NotNull PositionalArgumentStub stub) {
        return new PositionalArgumentImpl(stub, this);
    }

    @NotNull
    @Override
    public PositionalArgumentStub createStub(@NotNull PositionalArgument psi, StubElement<? extends PsiElement> parentStub) {
        return new PositionalArgumentStubImpl(parentStub, psi.getContent(), psi.isImportArgument());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof PositionalArgument;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull PositionalArgumentStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getValue());
        dataStream.writeBoolean(stub.isImportArgument());
    }

    @NotNull
    @Override
    public PositionalArgumentStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        String value = dataStream.readNameString();
        boolean isImportArgument = dataStream.readBoolean();
        return new PositionalArgumentStubImpl(parentStub, value, isImportArgument);
    }

    @Override
    public void indexStub(@NotNull PositionalArgumentStub stub, @NotNull IndexSink sink) {
        if (stub.isImportArgument()) {
            sink.occurrence(PositionalArgumentImportIndex.getInstance().getKey(), stub.getValue().replace('/', '.').toLowerCase());
        }
    }
}
