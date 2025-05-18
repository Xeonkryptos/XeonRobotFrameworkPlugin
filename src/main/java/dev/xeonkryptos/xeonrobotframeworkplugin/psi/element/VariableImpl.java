package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.IStubElementType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotVariableReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.element.VariableStub;
import org.jetbrains.annotations.NotNull;

public class VariableImpl extends RobotStubPsiElementBase<VariableStub, Variable> implements Variable {

    public VariableImpl(@NotNull ASTNode node) {
        super(node);
    }

    public VariableImpl(@NotNull VariableStub stub, @NotNull IStubElementType<VariableStub, Variable> nodeType) {
        super(stub, nodeType);
    }

    @NotNull
    @Override
    public PsiReference getReference() {
        return new RobotVariableReference(this);
    }

    @NotNull
    @Override
    public String getName() {
        VariableStub stub = getStub();
        if (stub != null) {
            return stub.getName();
        }
        return InjectedLanguageManager.getInstance(getProject()).getUnescapedText(this);
    }

    @NotNull
    @Override
    public String getUnwrappedName() {
        VariableStub stub = getStub();
        if (stub != null) {
            return stub.getUnwrappedName();
        }
        return Variable.super.getUnwrappedName();
    }

    @Override
    public final boolean isNested() {
        String text = getName();
        return StringUtil.getOccurrenceCount(text, "}") > 1 &&
               StringUtil.getOccurrenceCount(text, "${") + StringUtil.getOccurrenceCount(text, "@{") + StringUtil.getOccurrenceCount(text, "%{") > 1;
    }

    @Override
    public boolean isEmpty() {
        return getName().length() <= 3;
    }
}
