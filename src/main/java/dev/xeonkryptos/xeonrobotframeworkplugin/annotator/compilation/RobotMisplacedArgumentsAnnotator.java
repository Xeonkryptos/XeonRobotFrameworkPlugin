package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.RobotAnnotator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RobotMisplacedArgumentsAnnotator extends RobotAnnotator {

    @Override
    public void visitPositionalArgument(@NotNull RobotPositionalArgument element) {
        RobotKeywordCall keywordCall = PsiTreeUtil.getParentOfType(element, RobotKeywordCall.class, true, RobotParameter.class);
        if (keywordCall != null) {
            keywordCall.getStartOfKeywordsOnlyIndex().ifPresent(keywordsOnlyIndex -> handlePositionalArgumentWithinKeywordOnlyKeywordCall(keywordsOnlyIndex, element, keywordCall, getHolder()));
        }
    }

    @Override
    public void visitParameter(@NotNull RobotParameter parameter) {
        RobotKeywordCall keywordCall = PsiTreeUtil.getParentOfType(parameter, RobotKeywordCall.class);
        if (keywordCall != null) {
            if (parameter.isFakeParameter()) {
                keywordCall.getStartOfKeywordsOnlyIndex().ifPresent(keywordsOnlyIndex -> handlePositionalArgumentWithinKeywordOnlyKeywordCall(keywordsOnlyIndex, parameter, keywordCall, getHolder()));
            } else if (!keywordCall.hasPositionalArgumentsContainer()) {
                keywordCall.getPositionalArgumentsOnlyEndIndex().ifPresent(positionalArgumentsOnlyEndIndex -> {
                    int index = 0;
                    Collection<RobotArgument> allCallArguments = keywordCall.getAllCallArguments();
                    for (RobotArgument argument : allCallArguments) {
                        if (argument == parameter || index >= positionalArgumentsOnlyEndIndex) {
                            break;
                        }
                        index++;
                    }
                    if (index < positionalArgumentsOnlyEndIndex) {
                        highlightExpectedPositionalArgumentButParameterFound(parameter, getHolder());
                    }
                });
            }
        }
    }

    private static void handlePositionalArgumentWithinKeywordOnlyKeywordCall(int keywordsOnlyIndex, PsiElement positionalArgument, RobotKeywordCall keywordCall, @NotNull AnnotationHolder holder) {
        if (keywordsOnlyIndex == 0) {
            highlightExpectedParameterButPositionalArgumentFound(positionalArgument, holder);
        } else {
            int index = 0;
            Collection<RobotArgument> allCallArguments = keywordCall.getAllCallArguments();
            for (RobotArgument argument : allCallArguments) {
                if (argument == positionalArgument) {
                    break;
                }
                index++;
            }
            if (index >= keywordsOnlyIndex) {
                highlightExpectedParameterButPositionalArgumentFound(positionalArgument, holder);
            }
        }
    }

    private static void highlightExpectedParameterButPositionalArgumentFound(PsiElement positionalArgument, AnnotationHolder holder) {
        holder.newAnnotation(HighlightSeverity.GENERIC_SERVER_ERROR_OR_WARNING, RobotBundle.message("annotation.keyword.positional-arguments.keyword-only.misplaced"))
              .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
              .range(positionalArgument)
              .create();
    }

    private static void highlightExpectedPositionalArgumentButParameterFound(RobotParameter parameter, AnnotationHolder holder) {
        holder.newAnnotation(HighlightSeverity.GENERIC_SERVER_ERROR_OR_WARNING, RobotBundle.message("annotation.keyword.parameters.positional-argument-only.misplaced"))
              .highlightType(ProblemHighlightType.GENERIC_ERROR_OR_WARNING)
              .range(parameter)
              .create();
    }
}
