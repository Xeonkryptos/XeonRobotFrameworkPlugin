package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotStatement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RobotArgumentAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RobotPositionalArgument positionalArgument)) {
            return;
        }
        RobotStatement robotStatement = PsiTreeUtil.getParentOfType(element, RobotParameter.class, RobotKeywordCall.class);
        if (robotStatement instanceof RobotKeywordCall keywordCall) {
            keywordCall.getStartOfKeywordsOnlyIndex()
                       .ifPresent(keywordsOnlyIndex -> handlePositionalArgumentWithinKeywordOnlyKeywordCall(keywordsOnlyIndex,
                                                                                                            positionalArgument,
                                                                                                            keywordCall,
                                                                                                            holder));
        }
    }

    private static void handlePositionalArgumentWithinKeywordOnlyKeywordCall(int keywordsOnlyIndex,
                                                                             RobotPositionalArgument positionalArgument,
                                                                             RobotKeywordCall keywordCall,
                                                                             @NotNull AnnotationHolder holder) {
        if (keywordsOnlyIndex == 0) {
            highlightPositionalArgumentAsInvalidlyPlaced(positionalArgument, holder);
        } else {
            int index = 0;
            Collection<RobotArgument> allCallArguments = keywordCall.getAllCallArguments();
            for (RobotArgument argument : allCallArguments) {
                if (argument == positionalArgument) {
                    break;
                }
                index++;
            }
            if (index != allCallArguments.size() && index >= keywordsOnlyIndex) {
                highlightPositionalArgumentAsInvalidlyPlaced(positionalArgument, holder);
            }
        }
    }

    private static void highlightPositionalArgumentAsInvalidlyPlaced(RobotPositionalArgument positionalArgument, AnnotationHolder holder) {
        holder.newAnnotation(HighlightSeverity.GENERIC_SERVER_ERROR_OR_WARNING, RobotBundle.getMessage("annotation.keyword.positional-arguments.keyword-only.misplaced"))
              .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
              .range(positionalArgument)
              .create();
    }
}
