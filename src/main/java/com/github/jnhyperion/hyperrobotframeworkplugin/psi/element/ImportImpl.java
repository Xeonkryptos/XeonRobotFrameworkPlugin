package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class ImportImpl extends RobotPsiElementBase implements Import {

    public ImportImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public final boolean isResource() {
        return this.getPresentableText().equals("Resource");
    }

    @Override
    public final boolean isVariables() {
        return this.getPresentableText().equals("Variables");
    }

    @Override
    public final String d() {
        PsiElement[] children = getChildren();
        return this.isResource() && children.length > 0 ? this.getPresentableText() + "    " + children[0].getText() : this.getText();
    }

    @Override
    public final boolean isLibrary() {
        return this.getPresentableText().equals("Library");
    }
}
