package com.github.jnhyperion.hyperrobotframeworkplugin.ide.completion;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Heading;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

class GherkinCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        if (CompletionProviderUtils.isIndexPositionAWhitespaceCharacter(parameters)) {
            Heading heading = CompletionProviderUtils.getHeading(parameters.getPosition());
            if (heading != null && heading.containsTestCases()) {
                CompletionProviderUtils.addSyntaxLookup(RobotTokenTypes.GHERKIN, result);
            }
        }
    }
}
