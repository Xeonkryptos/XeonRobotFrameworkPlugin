package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotVariableDefinitionImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableDefinitionNameIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RobotVariableDefinitionStubElement extends IStubElementType<RobotVariableDefinitionStub, RobotVariableDefinition> {

    public RobotVariableDefinitionStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    public static RobotVariableDefinitionStubElement create(@NotNull @NonNls String debugName) {
        return new RobotVariableDefinitionStubElement(debugName);
    }

    @Override
    public RobotVariableDefinition createPsi(@NotNull RobotVariableDefinitionStub stub) {
        return new RobotVariableDefinitionImpl(stub, this);
    }

    @NotNull
    @Override
    public RobotVariableDefinitionStub createStub(@NotNull RobotVariableDefinition psi, StubElement<? extends PsiElement> parentStub) {
        return new RobotVariableDefinitionStubImpl(parentStub, psi.getName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof RobotVariableDefinition variableDefinition && variableDefinition.getName() != null && !variableDefinition.getName()
                                                                                                                                                 .isBlank();
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull RobotVariableDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @NotNull
    @Override
    public RobotVariableDefinitionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new RobotVariableDefinitionStubImpl(parentStub, dataStream.readNameString());
    }

    @Override
    public void indexStub(@NotNull RobotVariableDefinitionStub stub, @NotNull IndexSink sink) {
        String variableNameInLowerCase = stub.getName().toLowerCase();
        sink.occurrence(VariableDefinitionNameIndex.KEY, variableNameInLowerCase);
    }
}
