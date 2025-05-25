package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinitionImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableDefinitionNameIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class VariableDefinitionStubElement extends IStubElementType<VariableDefinitionStub, VariableDefinition> {

    public VariableDefinitionStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    @Override
    public VariableDefinition createPsi(@NotNull VariableDefinitionStub stub) {
        return new VariableDefinitionImpl(stub, this);
    }

    @NotNull
    @Override
    public VariableDefinitionStub createStub(@NotNull VariableDefinition psi, StubElement<? extends PsiElement> parentStub) {
        return new VariableDefinitionStubImpl(parentStub, psi.getName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof VariableDefinition;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull VariableDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @NotNull
    @Override
    public VariableDefinitionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new VariableDefinitionStubImpl(parentStub, dataStream.readNameString());
    }

    @Override
    public void indexStub(@NotNull VariableDefinitionStub stub, @NotNull IndexSink sink) {
        if (!stub.isEmpty()) {
            String unwrappedVariableNameInLowerCase = stub.getUnwrappedName().toLowerCase();
            sink.occurrence(VariableDefinitionNameIndex.KEY, unwrappedVariableNameInLowerCase);
        }
    }
}
