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
        return "Resource".equals(this.getPresentableText());
    }

    @Override
    public final boolean isVariables() {
        return "Variables".equals(this.getPresentableText());
    }

    @Override
    public final String getImportText() {
        PsiElement[] children = getChildren();
        return isResource() && children.length > 0 ? getPresentableText() + "    " + children[0].getText() : getText();
    }

    @Override
    public final boolean isLibrary() {
        return "Library".equals(this.getPresentableText());
    }
}
