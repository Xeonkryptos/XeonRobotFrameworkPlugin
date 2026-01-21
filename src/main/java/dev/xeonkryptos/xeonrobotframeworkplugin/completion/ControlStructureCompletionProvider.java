package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

class ControlStructureCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final IElementType controlStructureType;

    ControlStructureCompletionProvider(IElementType controlStructureType) {
        this.controlStructureType = controlStructureType;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        List<LookupElement> lookupElements = CompletionProviderUtils.computeAdditionalSyntaxLookups(controlStructureType);
        result.addAllElements(lookupElements);
    }
}
