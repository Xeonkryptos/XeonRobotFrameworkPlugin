package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Heading;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

class BracketSettingsCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        if (CompletionProviderUtils.isIndexPositionAWhitespaceCharacter(parameters)) {
            Heading heading = CompletionProviderUtils.getHeading(parameters.getPosition());
            if (heading != null && (heading.containsTestCases() || heading.containsKeywordDefinitions())) {
                CompletionProviderUtils.addSyntaxLookup(RobotTokenTypes.BRACKET_SETTING, result);
            }
        }
    }
}
