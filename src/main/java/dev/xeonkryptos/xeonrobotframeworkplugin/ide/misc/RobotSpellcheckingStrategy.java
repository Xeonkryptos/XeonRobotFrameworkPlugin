package dev.xeonkryptos.xeonrobotframeworkplugin.ide.misc;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.TokenConsumer;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import org.jetbrains.annotations.NotNull;

public class RobotSpellcheckingStrategy extends SpellcheckingStrategy {

    @NotNull
    @Override
    public Tokenizer<?> getTokenizer(PsiElement element) {
        IElementType elementType;
        if (element instanceof LeafPsiElement leafPsiElement) {
            elementType = leafPsiElement.getElementType();
            if (elementType == RobotTypes.PARAMETER ||
                elementType == RobotTypes.POSITIONAL_ARGUMENT ||
                elementType == RobotTypes.VARIABLE ||
                elementType == RobotTypes.VARIABLE_DEFINITION ||
                elementType == RobotTypes.USER_KEYWORD_STATEMENT ||
                elementType == RobotTypes.KEYWORD_CALL) {
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
