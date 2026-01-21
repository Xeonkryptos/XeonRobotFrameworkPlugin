package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.util.ProcessingContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

class SectionCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final Set<String> excludedSections = Set.of("*** Test Cases ***", "*** Tasks ***");

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        Collection<LookupElement> lookupElements = new LinkedList<>();
        boolean isResource = parameters.getOriginalFile().getFileType() == RobotResourceFileType.getInstance();
        for (LookupElement element : CompletionProviderUtils.computeAdditionalSyntaxLookups(RobotTypes.SECTION)) {
            String lookupString = element.getLookupString();
            if (!isResource || !excludedSections.contains(lookupString)) {
                lookupElements.add(element);
            }
        }
        result.addAllElements(lookupElements);
    }
}
