package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotPositionalArgumentImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.PositionalArgumentImportIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotImportArgumentIdentifier;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RobotPositionalArgumentStubElement extends IStubElementType<RobotPositionalArgumentStub, RobotPositionalArgument> {

    public RobotPositionalArgumentStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    public static RobotPositionalArgumentStubElement create(@NotNull @NonNls String debugName) {
        return new RobotPositionalArgumentStubElement(debugName);
    }

    @Override
    public RobotPositionalArgument createPsi(@NotNull RobotPositionalArgumentStub stub) {
        return new RobotPositionalArgumentImpl(stub, this);
    }

    @NotNull
    @Override
    public RobotPositionalArgumentStub createStub(@NotNull RobotPositionalArgument psi, StubElement<? extends PsiElement> parentStub) {
        String argumentText = InjectedLanguageManager.getInstance(psi.getProject()).getUnescapedText(psi);
        RobotImportArgumentIdentifier robotImportArgumentIdentifier = new RobotImportArgumentIdentifier();
        psi.accept(robotImportArgumentIdentifier);
        return new RobotPositionalArgumentStubImpl(parentStub, argumentText, robotImportArgumentIdentifier.isImportArgument());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof RobotPositionalArgument;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull RobotPositionalArgumentStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getValue());
        dataStream.writeBoolean(stub.isImportArgument());
    }

    @NotNull
    @Override
    public RobotPositionalArgumentStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        String value = dataStream.readNameString();
        boolean isImportArgument = dataStream.readBoolean();
        return new RobotPositionalArgumentStubImpl(parentStub, value, isImportArgument);
    }

    @Override
    public void indexStub(@NotNull RobotPositionalArgumentStub stub, @NotNull IndexSink sink) {
        if (stub.isImportArgument()) {
            String value = stub.getValue();
            value = value.replace('/', '.').toLowerCase();
            sink.occurrence(PositionalArgumentImportIndex.KEY, value);
        }
    }
}
