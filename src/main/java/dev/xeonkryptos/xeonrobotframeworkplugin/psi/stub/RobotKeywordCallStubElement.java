package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatementImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotKeywordCallImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.KeywordStatementStubImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.KeywordStatementNameIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RobotKeywordCallStubElement extends IStubElementType<RobotKeywordCallStub, RobotKeywordCall> {

    public RobotKeywordCallStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    public static RobotKeywordCallStubElement create(@NotNull @NonNls String debugName) {
        return new RobotKeywordCallStubElement(debugName);
    }

    @Override
    public RobotKeywordCall createPsi(@NotNull RobotKeywordCallStub stub) {
        return new RobotKeywordCallImpl(stub, this);
    }

    @NotNull
    @Override
    public RobotKeywordCallStub createStub(@NotNull RobotKeywordCall psi, StubElement<? extends PsiElement> parentStub) {
        return new RobotKeywordCallStubImpl(parentStub, psi.getName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof RobotKeywordCall;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull RobotKeywordCallStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @NotNull
    @Override
    public RobotKeywordCallStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new RobotKeywordCallStubImpl(parentStub, dataStream.readNameString());
    }

    @Override
    public void indexStub(@NotNull RobotKeywordCallStub stub, @NotNull IndexSink sink) {
        String keywordNameInLowerCase = stub.getName().toLowerCase();
        sink.occurrence(KeywordStatementNameIndex.KEY, keywordNameInLowerCase);
    }
}
