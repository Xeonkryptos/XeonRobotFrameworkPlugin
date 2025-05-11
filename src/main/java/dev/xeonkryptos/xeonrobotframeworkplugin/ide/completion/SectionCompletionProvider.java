package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenTypes;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

class SectionCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final Set<String> excludedSections = Set.of("*** Test Cases ***", "*** Tasks ***", "*** Test Case ***", "*** Task ***");

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        if (CompletionProviderUtils.isIndexPositionStartOfLine(parameters)) {
            boolean isResource = parameters.getOriginalFile().getFileType() == RobotResourceFileType.getInstance();
            for (LookupElement element : CompletionProviderUtils.computeAdditionalSyntaxLookups(RobotTokenTypes.HEADING)) {
                String lookupString = element.getLookupString();
                if (!isResource || !excludedSections.contains(lookupString)) {
                    result.addElement(element);
                }
            }
        }
    }
}
