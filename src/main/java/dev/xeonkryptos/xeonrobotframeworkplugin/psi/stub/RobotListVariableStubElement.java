package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotListVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotListVariableImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.VariableNameUtil;
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
        RobotVariableBodyId variableBodyId = RobotPsiUtil.getVariableBodyId(psi);
        String variableName = variableBodyId != null ? variableBodyId.getText() : null;
        return new RobotListVariableStubImpl(parentStub, variableName);
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        RobotListVariable listVariable = node.getPsi(RobotListVariable.class);
        String variableName = listVariable.getVariableName();
        return variableName != null && !variableName.isBlank() && !(listVariable.getParent() instanceof RobotVariableDefinition);
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
            VariableNameUtil.INSTANCE.computeVariableNameVariants(variableName).forEach(variant -> sink.occurrence(VariableNameIndex.KEY, variant));
        }
    }
}
