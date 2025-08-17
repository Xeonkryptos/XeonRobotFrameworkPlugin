package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

class StandardTagCompletionProvider extends CompletionProvider<CompletionParameters> {

    // TODO: Filter tags based on robot version currently installed (on project-level)
    private static final String[] STANDARD_TAGS = { "robot:continue-on-failure",
                                                    "robot:recursive-continue-on-failure",
                                                    "robot:stop-on-failure", // New in Robot Framework 6.0
                                                    "robot:recursive-stop-on-failure", // New in Robot Framework 6.0
                                                    "robot:exit-on-failure",  // New in Robot Framework 7.0
                                                    "robot:skip-on-failure",
                                                    "robot:skip",
                                                    "robot:exclude",
                                                    "robot:private",  // New in Robot Framework 6.0
                                                    "robot:no-dry-run",
                                                    "robot:exit",
                                                    "robot:flatten" // New in Robot Framework 6.1
    };

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        for (String standardTag : STANDARD_TAGS) {
            LookupElement lookupElement = LookupElementBuilder.create(standardTag);
            result.addElement(lookupElement);
        }
    }
}
