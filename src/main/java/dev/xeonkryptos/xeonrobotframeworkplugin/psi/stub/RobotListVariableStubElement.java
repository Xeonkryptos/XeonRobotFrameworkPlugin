package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotListVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotListVariableImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableNameIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RobotListVariableStubElement extends IStubElementType<RobotListVariableStub, RobotListVariable> {

    public RobotListVariableStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    public static RobotListVariableStubElement create(@NotNull @NonNls String debugName) {
        return new RobotListVariableStubElement(debugName);
    }

    @Override
    public RobotListVariable createPsi(@NotNull RobotListVariableStub stub) {
        return new RobotListVariableImpl(stub, this);
    }

    @NotNull
    @Override
    public RobotListVariableStub createStub(@NotNull RobotListVariable psi, StubElement<? extends PsiElement> parentStub) {
        RobotVariableBodyId variableBodyId = PsiTreeUtil.getChildOfType(psi, RobotVariableBodyId.class);
        String variableName = variableBodyId != null ? variableBodyId.getText() : null;
        return new RobotListVariableStubImpl(parentStub, variableName);
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof RobotListVariable;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull RobotListVariableStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        String variableName = stub.getVariableName();
        dataStream.writeName(variableName);
    }

    @NotNull
    @Override
    public RobotListVariableStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new RobotListVariableStubImpl(parentStub, dataStream.readNameString());
    }

    @Override
    public void indexStub(@NotNull RobotListVariableStub stub, @NotNull IndexSink sink) {
        String variableName = stub.getVariableName();
        if (variableName != null) {
            String variableNameInLowerCase = variableName.toLowerCase();
            sink.occurrence(VariableNameIndex.KEY, variableNameInLowerCase);
        }
    }
}
