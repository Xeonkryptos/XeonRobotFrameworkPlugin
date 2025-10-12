package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotUserKeywordStatementImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.KeywordDefinitionNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordNameUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RobotUserKeywordStubElement extends IStubElementType<RobotUserKeywordStub, RobotUserKeywordStatement> {

    public RobotUserKeywordStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    public static RobotUserKeywordStubElement create(@NotNull @NonNls String debugName) {
        return new RobotUserKeywordStubElement(debugName);
    }

    @Override
    public RobotUserKeywordStatement createPsi(@NotNull RobotUserKeywordStub stub) {
        return new RobotUserKeywordStatementImpl(stub, this);
    }

    @NotNull
    @Override
    public RobotUserKeywordStub createStub(@NotNull RobotUserKeywordStatement psi, StubElement<? extends PsiElement> parentStub) {
        return new RobotUserKeywordStubImpl(parentStub, psi.getName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof RobotUserKeywordStatement;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull RobotUserKeywordStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getName());
    }

    @NotNull
    @Override
    public RobotUserKeywordStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new RobotUserKeywordStubImpl(parentStub, dataStream.readNameString());
    }

    @Override
    public void indexStub(@NotNull RobotUserKeywordStub stub, @NotNull IndexSink sink) {
        String name = stub.getName();
        String normalizeKeywordName = KeywordNameUtil.normalizeKeywordName(name);
        sink.occurrence(KeywordDefinitionNameIndex.KEY, normalizeKeywordName);
    }
}
