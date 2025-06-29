package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotInlineVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSingleVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotInlineVariableStatementImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotSingleVariableStatementImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableDefinitionNameIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RobotInlineVariableStatementStubElement extends IStubElementType<RobotInlineVariableStatementStub, RobotInlineVariableStatement> {

    public RobotInlineVariableStatementStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    public static RobotInlineVariableStatementStubElement create(@NotNull @NonNls String debugName) {
        return new RobotInlineVariableStatementStubElement(debugName);
    }

    @Override
    public RobotInlineVariableStatement createPsi(@NotNull RobotInlineVariableStatementStub stub) {
        return new RobotInlineVariableStatementImpl(stub, this);
    }

    @NotNull
    @Override
    public RobotInlineVariableStatementStub createStub(@NotNull RobotInlineVariableStatement psi, StubElement<? extends PsiElement> parentStub) {
        return new RobotInlineVariableStatementStubImpl(parentStub, psi.getName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof RobotInlineVariableStatement;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull RobotInlineVariableStatementStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @NotNull
    @Override
    public RobotInlineVariableStatementStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new RobotInlineVariableStatementStubImpl(parentStub, dataStream.readNameString());
    }

    @Override
    public void indexStub(@NotNull RobotInlineVariableStatementStub stub, @NotNull IndexSink sink) {
        if (!stub.isEmpty()) {
            String unwrappedVariableNameInLowerCase = stub.getUnwrappedName().toLowerCase();
            sink.occurrence(VariableDefinitionNameIndex.KEY, unwrappedVariableNameInLowerCase);
        }
    }
}
