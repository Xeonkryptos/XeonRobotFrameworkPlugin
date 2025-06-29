package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSingleVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinitionImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotSingleVariableStatementImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.VariableDefinitionStub;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.VariableDefinitionStubImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableDefinitionNameIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RobotSingleVariableStatementStubElement extends IStubElementType<RobotSingleVariableStatementStub, RobotSingleVariableStatement> {

    public RobotSingleVariableStatementStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    public static RobotSingleVariableStatementStubElement create(@NotNull @NonNls String debugName) {
        return new RobotSingleVariableStatementStubElement(debugName);
    }

    @Override
    public RobotSingleVariableStatement createPsi(@NotNull RobotSingleVariableStatementStub stub) {
        return new RobotSingleVariableStatementImpl(stub, this);
    }

    @NotNull
    @Override
    public RobotSingleVariableStatementStub createStub(@NotNull RobotSingleVariableStatement psi, StubElement<? extends PsiElement> parentStub) {
        return new RobotSingleVariableStatementStubImpl(parentStub, psi.getName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof RobotSingleVariableStatement;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull RobotSingleVariableStatementStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @NotNull
    @Override
    public RobotSingleVariableStatementStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new RobotSingleVariableStatementStubImpl(parentStub, dataStream.readNameString());
    }

    @Override
    public void indexStub(@NotNull RobotSingleVariableStatementStub stub, @NotNull IndexSink sink) {
        if (!stub.isEmpty()) {
            String unwrappedVariableNameInLowerCase = stub.getUnwrappedName().toLowerCase();
            sink.occurrence(VariableDefinitionNameIndex.KEY, unwrappedVariableNameInLowerCase);
        }
    }
}
