package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.QualifiedNameBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.Icon;

public abstract class RobotPsiElementBase extends ASTWrapperPsiElement implements RobotElement {

    public RobotPsiElementBase(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {

            @Override
            public String getPresentableText() {
                String name = RobotPsiElementBase.this.getName();
                if (name == null) {
                    String text = getText();
                    return text.lines().findFirst().orElse(null);
                }
                return name;
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

    @Override
    public String toString() {
        return super.toString() + "(" + getText() + ")";
    }
}
