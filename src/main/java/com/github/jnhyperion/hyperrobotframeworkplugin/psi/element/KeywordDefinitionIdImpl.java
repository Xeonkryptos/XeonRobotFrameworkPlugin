package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class KeywordDefinitionIdImpl extends RobotPsiElementBase implements KeywordDefinitionId {

    private String name;

   public KeywordDefinitionIdImpl(@NotNull ASTNode node) {
         super(node);

        name = getPresentableText();
   }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        name = getPresentableText();
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }
}
