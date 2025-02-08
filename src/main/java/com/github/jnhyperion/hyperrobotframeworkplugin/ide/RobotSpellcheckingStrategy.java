package com.github.jnhyperion.hyperrobotframeworkplugin.ide;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.TokenConsumer;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import org.jetbrains.annotations.NotNull;

public class RobotSpellcheckingStrategy extends SpellcheckingStrategy {

    @NotNull
    @Override
    public Tokenizer<?> getTokenizer(PsiElement element) {
        IElementType elementType;
        if (element instanceof LeafPsiElement) {
            elementType = ((LeafPsiElement) element).getElementType();
            if (elementType == RobotTokenTypes.PARAMETER ||
                elementType == RobotTokenTypes.ARGUMENT ||
                elementType == RobotTokenTypes.VARIABLE ||
                elementType == RobotTokenTypes.VARIABLE_DEFINITION ||
                elementType == RobotTokenTypes.KEYWORD ||
                elementType == RobotTokenTypes.KEYWORD_DEFINITION ||
                elementType == RobotTokenTypes.KEYWORD_STATEMENT) {
                return new LeafPsiElementTokenizer();
            }
        }

        return super.getTokenizer(element);
    }

    private static class LeafPsiElementTokenizer extends Tokenizer<LeafPsiElement> {

        @Override
        public void tokenize(@NotNull LeafPsiElement element, @NotNull TokenConsumer consumer) {
        }
    }
}
