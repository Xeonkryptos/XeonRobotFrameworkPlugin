package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotDictVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotEnvironmentVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotListVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotScalarVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyContent;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl.RobotKeywordVariableStatementImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableDefinitionNameIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RobotKeywordVariableStatementStubElement extends IStubElementType<RobotKeywordVariableStatementStub, RobotKeywordVariableStatement> {

    public RobotKeywordVariableStatementStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    public static RobotKeywordVariableStatementStubElement create(@NotNull @NonNls String debugName) {
        return new RobotKeywordVariableStatementStubElement(debugName);
    }

    @Override
    public RobotKeywordVariableStatement createPsi(@NotNull RobotKeywordVariableStatementStub stub) {
        return new RobotKeywordVariableStatementImpl(stub, this);
    }

    @NotNull
    @Override
    public RobotKeywordVariableStatementStub createStub(@NotNull RobotKeywordVariableStatement psi, StubElement<? extends PsiElement> parentStub) {
        RobotKeywordVariableNameCollector variableNameCollector = new RobotKeywordVariableNameCollector();
        psi.accept(variableNameCollector);
        String[] variableNames = variableNameCollector.variableNames.toArray(String[]::new);
        return new RobotKeywordVariableStatementStubImpl(parentStub, variableNames);
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof RobotKeywordVariableStatement;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull RobotKeywordVariableStatementStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        for (String variableName : stub.getVariableNames()) {
            dataStream.writeName(variableName);
        }
    }

    @NotNull
    @Override
    public RobotKeywordVariableStatementStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        String readName;
        List<String> variableNames = new ArrayList<>();
        while ((readName = dataStream.readNameString()) != null) {
            variableNames.add(readName);
        }
        String[] variableNamesArray = variableNames.toArray(String[]::new);
        return new RobotKeywordVariableStatementStubImpl(parentStub, variableNamesArray);
    }

    @Override
    public void indexStub(@NotNull RobotKeywordVariableStatementStub stub, @NotNull IndexSink sink) {
        for (String variableName : stub.getVariableNames()) {
            String variableNameInLowerCase = variableName.toLowerCase();
            sink.occurrence(VariableDefinitionNameIndex.KEY, variableNameInLowerCase);
        }
    }

    private static class RobotKeywordVariableNameCollector extends RobotVisitor {

        private final List<String> variableNames = new ArrayList<>();

        @Override
        public void visitKeywordVariableStatement(@NotNull RobotKeywordVariableStatement o) {
            o.acceptChildren(this);
        }

        @Override
        public void visitVariable(@NotNull RobotVariable o) {
            o.acceptChildren(this);
        }

        @Override
        public void visitScalarVariable(@NotNull RobotScalarVariable o) {
            o.acceptChildren(this);
        }

        @Override
        public void visitDictVariable(@NotNull RobotDictVariable o) {
            o.acceptChildren(this);
        }

        @Override
        public void visitListVariable(@NotNull RobotListVariable o) {
            o.acceptChildren(this);
        }

        @Override
        public void visitEnvironmentVariable(@NotNull RobotEnvironmentVariable o) {
            o.acceptChildren(this);
        }

        @Override
        public void visitVariableId(@NotNull RobotVariableId o) {
            RobotVariableBodyContent variableBodyContent = o.getVariableBodyContent();
            if (variableBodyContent != null) {
                String variableName = InjectedLanguageManager.getInstance(o.getProject()).getUnescapedText(variableBodyContent);
                if (!variableName.isBlank()) {
                    variableNames.add(variableName);
                }
            }
        }
    }
}
