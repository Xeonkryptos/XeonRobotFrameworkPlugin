package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotImportArgumentImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.ImportArgumentIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RobotImportArgumentStubElement extends IStubElementType<RobotImportArgumentStub, RobotImportArgument> {

    public RobotImportArgumentStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    public static RobotImportArgumentStubElement create(@NotNull @NonNls String debugName) {
        return new RobotImportArgumentStubElement(debugName);
    }

    @Override
    public RobotImportArgument createPsi(@NotNull RobotImportArgumentStub stub) {
        return new RobotImportArgumentImpl(stub, this);
    }

    @NotNull
    @Override
    public RobotImportArgumentStub createStub(@NotNull RobotImportArgument psi, StubElement<? extends PsiElement> parentStub) {
        String argumentText = psi.getText();
        return new RobotImportArgumentStubImpl(parentStub, argumentText);
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull RobotImportArgumentStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getValue());
    }

    @NotNull
    @Override
    public RobotImportArgumentStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        String value = dataStream.readNameString();
        return new RobotImportArgumentStubImpl(parentStub, value);
    }

    @Override
    public void indexStub(@NotNull RobotImportArgumentStub stub, @NotNull IndexSink sink) {
        String value = stub.getValue();
        value = value.replace('/', '.').toLowerCase();
        sink.occurrence(ImportArgumentIndex.KEY, value);
    }
}
