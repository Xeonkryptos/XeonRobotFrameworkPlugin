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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotDictVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotDictVariableImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.VariableNameUtil;
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
        RobotVariableBodyId variableBodyId = RobotPsiImplUtil.getVariableBodyId(psi);
        String variableName = variableBodyId != null ? variableBodyId.getText() : null;
        return new RobotDictVariableStubImpl(parentStub, variableName);
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof RobotDictVariable variable && variable.getVariableName() != null
               && !(variable.getParent() instanceof RobotVariableDefinition);
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull RobotDictVariableStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        String variableName = stub.getVariableName();
        dataStream.writeName(variableName);
    }

    @NotNull
    @Override
    public RobotDictVariableStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new RobotDictVariableStubImpl(parentStub, dataStream.readNameString());
    }

    @Override
    public void indexStub(@NotNull RobotDictVariableStub stub, @NotNull IndexSink sink) {
        String variableName = stub.getVariableName();
        if (variableName != null) {
            VariableNameUtil.INSTANCE.computeVariableNameVariants(variableName).forEach(variant -> sink.occurrence(VariableNameIndex.KEY, variant));
        }
    }
}
