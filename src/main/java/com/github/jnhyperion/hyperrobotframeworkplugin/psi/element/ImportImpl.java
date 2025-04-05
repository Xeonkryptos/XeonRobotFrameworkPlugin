package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class ImportImpl extends RobotPsiElementBase implements Import {

    private final ImportType importType;

    public ImportImpl(@NotNull ASTNode node) {
        super(node);

        String presentableText = getPresentableText();
        if ("Library".equalsIgnoreCase(presentableText)) {
            importType = ImportType.LIBRARY;
        } else if ("Resource".equalsIgnoreCase(presentableText)) {
            importType = ImportType.RESOURCE;
        } else if ("Variables".equalsIgnoreCase(presentableText)) {
            importType = ImportType.VARIABLES;
        } else {
            importType = null;
        }
    }

    @Override
    public final boolean isLibrary() {
        return importType == ImportType.LIBRARY;
    }

    @Override
    public final boolean isResource() {
        return importType == ImportType.RESOURCE;
    }

    @Override
    public final boolean isVariables() {
        return importType == ImportType.VARIABLES;
    }

    @Override
    public final String getImportText() {
        PsiElement[] children = getChildren();
        return isResource() && children.length > 0 ? getPresentableText() + "    " + children[0].getText() : getText();
    }

    private enum ImportType {
        LIBRARY, RESOURCE, VARIABLES
    }
}
