package dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.QualifiedNameBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public abstract class RobotStubPsiElementBase<T extends StubElement<P>, P extends PsiElement> extends StubBasedPsiElementBase<T> {

    public RobotStubPsiElementBase(@NotNull ASTNode node) {
        super(node);
    }

    public RobotStubPsiElementBase(final T stub, final IStubElementType<T, P> nodeType) {
        super(stub, nodeType);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {

            @Override
            public String getPresentableText() {
                String name = RobotStubPsiElementBase.this.getName();
                if (name == null) {
                    String text = getText();
                    return text.lines().findFirst().orElse(null);
                }
                return name;
            }

            @Override
            public String getLocationString() {
                return QualifiedNameBuilder.computeQualifiedPath(RobotStubPsiElementBase.this);
            }

            @Override
            public Icon getIcon(boolean unused) {
                return RobotStubPsiElementBase.this.getIcon(ICON_FLAG_VISIBILITY);
            }
        };
    }

    @Override
    public String toString() {
        return super.toString() + "(" + getText() + ")";
    }
}
