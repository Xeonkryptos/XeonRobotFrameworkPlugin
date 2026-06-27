package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotEnumValuesResolver;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PositionalArgumentProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        RobotPositionalArgument currentPositionalArgument = PsiTreeUtil.getParentOfType(parameters.getPosition(), RobotPositionalArgument.class);
        if (currentPositionalArgument != null) {
            LookupElement[] enumValues = RobotEnumValuesResolver.findPossibleEnumValuesFor(currentPositionalArgument);
            result.addAllElements(List.of(enumValues));
        }
    }
}
