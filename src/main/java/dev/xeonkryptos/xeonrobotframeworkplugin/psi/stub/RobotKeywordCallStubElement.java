package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibrary;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotKeywordCallImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.KeywordCallNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordNameUtil;
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
        RobotKeywordCallName keywordCallName = psi.getKeywordCallName();
        RobotKeywordCallLibrary keywordCallLibrary = keywordCallName.getKeywordCallLibrary();
        String libraryName = keywordCallLibrary != null ? keywordCallLibrary.getKeywordCallLibraryName().getText() : null;
        String fullKeywordName = psi.getName();
        return new RobotKeywordCallStubImpl(parentStub, libraryName, fullKeywordName);
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
        String libraryName = stub.getLibraryName();
        String name = stub.getName();

        dataStream.writeName(libraryName);
        dataStream.writeName(name);
    }

    @NotNull
    @Override
    public RobotKeywordCallStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        String libraryName = dataStream.readNameString();
        String name = dataStream.readNameString();
        return new RobotKeywordCallStubImpl(parentStub, libraryName, name);
    }

    @Override
    public void indexStub(@NotNull RobotKeywordCallStub stub, @NotNull IndexSink sink) {
        String libraryName = stub.getLibraryName();
        String keywordName = stub.getName();
        keywordName = KeywordNameUtil.normalizeKeywordName(keywordName);
        sink.occurrence(KeywordCallNameIndex.KEY, keywordName);

        if (libraryName != null) {
            keywordName = keywordName.substring(libraryName.length() + 1);
            sink.occurrence(KeywordCallNameIndex.KEY, keywordName);
        }
    }
}
