package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotKeywordProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class ControlStructureCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        if (CompletionProviderUtils.isIndexPositionAWhitespaceCharacter(parameters)) {
            List<LookupElement> lookupElements = CompletionProviderUtils.computeAdditionalSyntaxLookups(RobotKeywordProvider.SYNTAX_MARKER);
            List<LookupElement> nonSpecialElements = new ArrayList<>();
            List<LookupElement> specialElements = new ArrayList<>();

            for (LookupElement element : lookupElements) {
                String lookupString = element.getLookupString();
                if (!"AS".equals(lookupString) && !lookupString.startsWith("IN")) {
                    nonSpecialElements.add(element);
                } else {
                    specialElements.add(element);
                }
            }

            if (!isArgument(parameters.getPosition())) {
                result.addAllElements(nonSpecialElements);
            } else {
                result.addAllElements(specialElements);
            }
        }
    }

    private boolean isArgument(PsiElement current) {
        if (current == null) {
            return false;
        }
        return current.getParent() instanceof RobotPositionalArgument;
    }
}
