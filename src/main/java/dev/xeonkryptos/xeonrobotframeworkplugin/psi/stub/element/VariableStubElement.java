package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLanguage;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Variable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableImpl;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableNameIndex;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VariableStubElement extends IStubElementType<VariableStub, Variable> {

    private static final Pattern VARIABLE_NAME_IN_EXTENDED_PATTERN = Pattern.compile("([\\p{L}\\p{N}_]+)", Pattern.UNICODE_CHARACTER_CLASS);

    public VariableStubElement(@NotNull @NonNls String debugName) {
        super(debugName, RobotLanguage.INSTANCE);
    }

    @Override
    public Variable createPsi(@NotNull VariableStub stub) {
        return new VariableImpl(stub, this);
    }

    @NotNull
    @Override
    public VariableStub createStub(@NotNull Variable psi, StubElement<? extends PsiElement> parentStub) {
        return new VariableStubImpl(parentStub, psi.getName());
    }

    @Override
    public boolean shouldCreateStub(ASTNode node) {
        return node.getPsi() instanceof Variable;
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "robot." + this;
    }

    @Override
    public void serialize(@NotNull VariableStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        String variableName = stub.getName();
        dataStream.writeName(variableName);
    }

    @NotNull
    @Override
    public VariableStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        return new VariableStubImpl(parentStub, dataStream.readNameString());
    }

    @Override
    public void indexStub(@NotNull VariableStub stub, @NotNull IndexSink sink) {
        if (!stub.isEmpty() && !stub.isEnvironmentVariable()) {
            String unwrappedVariableNameInLowerCase = stub.getUnwrappedName().toLowerCase();
            Matcher matcher = VARIABLE_NAME_IN_EXTENDED_PATTERN.matcher(unwrappedVariableNameInLowerCase);
            if (matcher.find()) {
                String simplifiedVariableName = matcher.group();
                if (!simplifiedVariableName.equals(unwrappedVariableNameInLowerCase)) {
                    sink.occurrence(VariableNameIndex.KEY, simplifiedVariableName);
                }
            }
            sink.occurrence(VariableNameIndex.KEY, unwrappedVariableNameInLowerCase);
        }
    }
}
