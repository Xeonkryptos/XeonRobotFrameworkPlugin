package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

public class RobotPatterns {

    public static <T extends PsiElement> PatternCondition<T> atFirstPositionOf(ElementPattern<? extends T> pattern) {
        return new PatternCondition<>("atFirstPositionOf") {
            @Override
            public boolean accepts(@NotNull T t, ProcessingContext context) {
                PsiElement previousElement = null;
                PsiElement element = t;
                while (element != null) {
                    if (pattern.accepts(element, context)) {
                        return true;
                    }
                    if (previousElement != null) {
                        if (element.getFirstChild() != previousElement) {
                            return false;
                        }
                    }
                    previousElement = element;
                    element = element.getContext();
                }
                return false;
            }
        };
    }
}
