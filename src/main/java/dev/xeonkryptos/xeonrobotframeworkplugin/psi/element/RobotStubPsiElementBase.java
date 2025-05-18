package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.PatternUtil;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public abstract class RobotStubPsiElementBase<T extends StubElement<P>, P extends PsiElement> extends StubBasedPsiElementBase<T> implements RobotStatement {

    public RobotStubPsiElementBase(@NotNull ASTNode node) {
        super(node);
    }

    public RobotStubPsiElementBase(final T stub, final IStubElementType<T, P> nodeType) {
        super(stub, nodeType);
    }

    @NotNull
    @Override
    public String getPresentableText() {
        T stub = getStub();
        String text;
        InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(getProject());
        if (stub != null) {
            P psiElement = stub.getPsi();
            text = injectedLanguageManager.getUnescapedText(psiElement);
        } else {
            text = injectedLanguageManager.getUnescapedText(this);
        }
        return RobotPsiElementBase.getPresentableText(text);
    }

    @NotNull
    public static String getPresentableText(String text) {
        return PatternUtil.getPresentableText(text);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {

            @Override
            public String getPresentableText() {
                return RobotStubPsiElementBase.this.getPresentableText();
            }

            @Override
            public String getLocationString() {
                return null;
            }

            @Override
            public Icon getIcon(boolean var1) {
                return RobotStubPsiElementBase.this.getIcon(ICON_FLAG_VISIBILITY);
            }
        };
    }

    @NotNull
    @Override
    public String getName() {
        return getPresentableText();
    }

    public PsiElement setName(@NotNull String newName) {
        return this;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + getPresentableText() + ")";
    }
}
