package com.github.jnhyperion.hyperrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class KeywordDefinitionIdImpl extends RobotPsiElementBase implements KeywordDefinitionId {

   public KeywordDefinitionIdImpl(@NotNull ASTNode node) {
         super(node);
   }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
    }
}
