package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.util.ProcessingContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopHeader;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class ForLoopControlStructureCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        RobotForLoopStructure forLoopStructure = findForLoopStructure(position);
        if (forLoopStructure != null) {
            RobotForLoopHeader forLoopHeader = forLoopStructure.getForLoopHeader();
            if (forLoopHeader.getForInElement() == null) {
                List<LookupElement> lookupElements = CompletionProviderUtils.computeAdditionalSyntaxLookups(RobotTypes.FOR_IN);
                result.addAllElements(lookupElements);
            }
        }
    }

    @Nullable
    private RobotForLoopStructure findForLoopStructure(PsiElement element) {
        PsiElement prevSibling = element.getPrevSibling();
        while (prevSibling != null) {
            if (!(prevSibling instanceof PsiWhiteSpace) && !(prevSibling instanceof PsiComment)) {
                return prevSibling instanceof RobotForLoopStructure forLoopStructure ? forLoopStructure : null;
            }
            prevSibling = prevSibling.getPrevSibling();
        }
        return null;
    }
}
