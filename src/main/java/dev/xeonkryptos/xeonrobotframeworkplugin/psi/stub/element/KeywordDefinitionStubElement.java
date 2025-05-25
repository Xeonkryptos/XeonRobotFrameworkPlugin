package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinitionImpl;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.KeywordDefinitionNameIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class KeywordDefinitionStubElement extends IStubElementType<KeywordDefinitionStub, KeywordDefinition> {

    public KeywordDefinitionStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    @Override
    public KeywordDefinition createPsi(@NotNull KeywordDefinitionStub stub) {
        return new KeywordDefinitionImpl(stub, this);
    }

    @NotNull
    @Override
    public KeywordDefinitionStub createStub(@NotNull KeywordDefinition psi, StubElement<? extends PsiElement> parentStub) {
        return new KeywordDefinitionStubImpl(parentStub, psi.getName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof KeywordDefinition;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull KeywordDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @NotNull
    @Override
    public KeywordDefinitionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new KeywordDefinitionStubImpl(parentStub, dataStream.readNameString());
    }

    @Override
    public void indexStub(@NotNull KeywordDefinitionStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        sink.occurrence(KeywordDefinitionNameIndex.KEY, name.toLowerCase());
        sink.occurrence(KeywordDefinitionNameIndex.KEY, name);
    }
}
