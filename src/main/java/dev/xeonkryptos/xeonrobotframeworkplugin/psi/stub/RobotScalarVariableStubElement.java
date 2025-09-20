package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiImplUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotScalarVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotScalarVariableImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.VariableNameUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RobotScalarVariableStubElement extends IStubElementType<RobotScalarVariableStub, RobotScalarVariable> {

    public RobotScalarVariableStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    public static RobotScalarVariableStubElement create(@NotNull @NonNls String debugName) {
        return new RobotScalarVariableStubElement(debugName);
    }

    @Override
    public RobotScalarVariable createPsi(@NotNull RobotScalarVariableStub stub) {
        return new RobotScalarVariableImpl(stub, this);
    }

    @NotNull
    @Override
    public RobotScalarVariableStub createStub(@NotNull RobotScalarVariable psi, StubElement<? extends PsiElement> parentStub) {
        RobotVariableBodyId variableBodyId = RobotPsiImplUtil.getVariableBodyId(psi);
        String variableName = variableBodyId != null ? variableBodyId.getText() : null;
        return new RobotScalarVariableStubImpl(parentStub, variableName);
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof RobotScalarVariable;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull RobotScalarVariableStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        String variableName = stub.getVariableName();
        dataStream.writeName(variableName);
    }

    @NotNull
    @Override
    public RobotScalarVariableStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new RobotScalarVariableStubImpl(parentStub, dataStream.readNameString());
    }

    @Override
    public void indexStub(@NotNull RobotScalarVariableStub stub, @NotNull IndexSink sink) {
        String variableName = stub.getVariableName();
        if (variableName != null) {
            VariableNameUtil.INSTANCE.computeVariableNameVariants(variableName).forEach(variant -> sink.occurrence(VariableNameIndex.KEY, variant));
        }
    }
}
