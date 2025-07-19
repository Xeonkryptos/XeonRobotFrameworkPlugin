package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.QualifiedNameBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public abstract class RobotPsiElementBase extends ASTWrapperPsiElement implements RobotStatement {

    public RobotPsiElementBase(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {

            @Override
            public String getPresentableText() {
                String name = RobotPsiElementBase.this.getName();
                return name != null ? name : getText();
            }

            @Override
            public String getLocationString() {
                return QualifiedNameBuilder.computeQualifiedPath(RobotPsiElementBase.this);
            }

            @Override
            public Icon getIcon(boolean unused) {
                return RobotPsiElementBase.this.getIcon(ICON_FLAG_VISIBILITY);
            }
        };
    }

    public PsiElement setName(@NotNull String newName) {
        return this;
    }

    @Override
    public String toString() {
        return super.toString() + "(" + getText() + ")";
    }
}
