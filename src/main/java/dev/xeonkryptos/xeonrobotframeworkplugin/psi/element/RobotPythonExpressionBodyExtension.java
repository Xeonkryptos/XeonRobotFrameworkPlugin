package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;

public interface RobotPythonExpressionBodyExtension extends PsiLanguageInjectionHost {

    @Override
    default boolean isValidHost() {
        return true;
    }

    @Override
    default PsiLanguageInjectionHost updateText(@NotNull String text) {
        return ElementManipulators.handleContentChange(this, text);
    }

    @Override
    @NotNull
    default LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
        return new LiteralTextEscaper<>(this) {

            @Override
            public boolean decode(@NotNull TextRange rangeInsideHost, @NotNull StringBuilder outChars) {
                outChars.append(rangeInsideHost.substring(myHost.getText()));
                return true;
            }

            @Override
            public int getOffsetInHost(int offsetInDecoded, @NotNull TextRange rangeInsideHost) {
                return rangeInsideHost.getStartOffset() + offsetInDecoded;
            }

            @NotNull
            @Override
            public TextRange getRelevantTextRange() {
                String text = myHost.getText();
                int offset = text.length() - text.stripLeading().length();
                int length = text.trim().length();
                return TextRange.from(offset, length);
            }

            @Override
            public boolean isOneLine() {
                return true;
            }
        };
    }
}
