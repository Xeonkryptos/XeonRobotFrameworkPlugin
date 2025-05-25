package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatementImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.KeywordStatementNameIndex;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class KeywordStatementStubElement extends IStubElementType<KeywordStatementStub, KeywordStatement> {

    public KeywordStatementStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    @Override
    public KeywordStatement createPsi(@NotNull KeywordStatementStub stub) {
        return new KeywordStatementImpl(stub, this);
    }

    @NotNull
    @Override
    public KeywordStatementStub createStub(@NotNull KeywordStatement psi, StubElement<? extends PsiElement> parentStub) {
        return new KeywordStatementStubImpl(parentStub, psi.getName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof KeywordStatement;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull KeywordStatementStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @NotNull
    @Override
    public KeywordStatementStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new KeywordStatementStubImpl(parentStub, dataStream.readNameString());
    }

    @Override
    public void indexStub(@NotNull KeywordStatementStub stub, @NotNull IndexSink sink) {
        String keywordNameInLowerCase = stub.getName().toLowerCase();
        sink.occurrence(KeywordStatementNameIndex.KEY, keywordNameInLowerCase);
    }
}
