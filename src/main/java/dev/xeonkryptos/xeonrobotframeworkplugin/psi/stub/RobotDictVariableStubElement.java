package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotDictVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotDictVariableImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableNameIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RobotDictVariableStubElement extends IStubElementType<RobotDictVariableStub, RobotDictVariable> {

    public RobotDictVariableStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    public static RobotDictVariableStubElement create(@NotNull @NonNls String debugName) {
        return new RobotDictVariableStubElement(debugName);
    }

    @Override
    public RobotDictVariable createPsi(@NotNull RobotDictVariableStub stub) {
        return new RobotDictVariableImpl(stub, this);
    }

    @NotNull
    @Override
    public RobotDictVariableStub createStub(@NotNull RobotDictVariable psi, StubElement<? extends PsiElement> parentStub) {
        return new RobotDictVariableStubImpl(parentStub, psi.getName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof RobotDictVariable;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull RobotDictVariableStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        String variableName = stub.getName();
        dataStream.writeName(variableName);
    }

    @NotNull
    @Override
    public RobotDictVariableStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new RobotDictVariableStubImpl(parentStub, dataStream.readNameString());
    }

    @Override
    public void indexStub(@NotNull RobotDictVariableStub stub, @NotNull IndexSink sink) {
        String variableName = stub.getName();
        if (variableName != null) {
            String variableNameInLowerCase = variableName.toLowerCase();
            sink.occurrence(VariableNameIndex.KEY, variableNameInLowerCase);
        }
    }
}
