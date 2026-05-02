package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotVariableDefinitionImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableDefinitionNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.VariableNameUtil;
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
        String variableName = psi.getName();
        VariableScope variableScope = psi.getScope();
        VariableType variableType = psi.getVariableType();
        return new RobotVariableDefinitionStubImpl(parentStub, variableName, variableScope, variableType);
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        RobotVariableDefinition variableDefinition = node.getPsi(RobotVariableDefinition.class);
        String definitionName = variableDefinition.getName();
        return definitionName != null && !definitionName.isBlank();
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull RobotVariableDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        String variableName = stub.getName();
        VariableScope variableScope = stub.getScope();
        String scopeName = variableScope.name();
        String variableType = stub.getVariableType().name();

        dataStream.writeName(variableName);
        dataStream.writeName(scopeName);
        dataStream.writeName(variableType);
    }

    @NotNull
    @Override
    public RobotVariableDefinitionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        String variableName = dataStream.readNameString();
        String scopeName = dataStream.readNameString();
        String type = dataStream.readNameString();
        VariableScope variableScope = VariableScope.valueOf(scopeName);
        VariableType variableType = type != null ? VariableType.valueOf(type) : VariableType.SCALAR;
        return new RobotVariableDefinitionStubImpl(parentStub, variableName, variableScope, variableType);
    }

    @Override
    public void indexStub(@NotNull RobotVariableDefinitionStub stub, @NotNull IndexSink sink) {
        String variableName = stub.getName();
        VariableNameUtil.INSTANCE.computeVariableNameVariants(variableName).forEach(variant -> sink.occurrence(VariableDefinitionNameIndex.KEY, variant));
    }
}
