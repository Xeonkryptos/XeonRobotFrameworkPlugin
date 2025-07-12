package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.util.ProcessingContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotKeywordProvider;
import org.jetbrains.annotations.NotNull;

class GherkinCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        if (CompletionProviderUtils.isIndexPositionAWhitespaceCharacter(parameters)) {
            CompletionProviderUtils.addSyntaxLookup(RobotKeywordProvider.GHERKIN, result);
        }
    }
}
