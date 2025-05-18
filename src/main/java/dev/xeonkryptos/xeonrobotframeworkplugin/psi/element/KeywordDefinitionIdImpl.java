package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class KeywordDefinitionIdImpl extends RobotPsiElementBase implements KeywordDefinitionId {

   public KeywordDefinitionIdImpl(@NotNull ASTNode node) {
         super(node);
   }
}
