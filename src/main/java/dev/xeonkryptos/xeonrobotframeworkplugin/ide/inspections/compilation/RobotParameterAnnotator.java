package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotHighlighter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RobotParameterAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RobotParameter parameter)) {
            return;
        }
        RobotKeywordCall keywordCall = PsiTreeUtil.getParentOfType(element, RobotKeywordCall.class);
        if (keywordCall != null) {
            Collection<DefinedParameter> availableParameters = keywordCall.getAvailableParameters();
            String parameterName = parameter.getParameterName();
            boolean directMatchFound = availableParameters.stream().anyMatch(param -> param.matches(parameterName));
            if (!directMatchFound && availableParameters.stream().noneMatch(DefinedParameter::isKeywordContainer)) {
                keywordCall.getStartOfKeywordsOnlyIndex()
                           .ifPresentOrElse(keywordsOnlyIndex -> handleParameterWithinKeywordOnlyKeywordCall(keywordsOnlyIndex, parameter, keywordCall, holder),
                                            () -> convertParameterToArgumentVisually(holder, parameter));
            }
        } else if (PsiTreeUtil.getParentOfType(parameter, RobotLocalSetting.class) != null) {
            convertParameterToArgumentVisually(holder, parameter);
        }
    }

    private static void handleParameterWithinKeywordOnlyKeywordCall(int keywordsOnlyIndex,
                                                                    RobotParameter parameter,
                                                                    RobotKeywordCall keywordCall,
                                                                    @NotNull AnnotationHolder holder) {
        String keywordCallName = keywordCall.getName();
        if (keywordsOnlyIndex == 0) {
            highlightParameterAsNotFound(parameter, keywordCallName, holder);
        } else {
            int index = 0;
            Collection<RobotArgument> allCallArguments = keywordCall.getAllCallArguments();
            for (RobotArgument argument : allCallArguments) {
                if (argument == parameter) {
                    break;
                }
                index++;
            }
            if (index >= keywordsOnlyIndex) {
                highlightParameterAsNotFound(parameter, keywordCallName, holder);
            } else {
                convertParameterToArgumentVisually(holder, parameter);
            }
        }
    }

    private static void highlightParameterAsNotFound(RobotParameter parameter, String keywordName, AnnotationHolder holder) {
        holder.newAnnotation(HighlightSeverity.WARNING, RobotBundle.message("annotation.keyword.parameter.not-found", keywordName))
              .highlightType(ProblemHighlightType.WARNING)
              .range(parameter.getParameterId())
              .create();
    }

    private static void convertParameterToArgumentVisually(@NotNull AnnotationHolder holder, RobotParameter parameter) {
        // Grow range by 1 to include the assignment operator
        TextRange parameterIdTextRange = parameter.getParameterId().getTextRange().grown(1);
        holder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).range(parameterIdTextRange).textAttributes(RobotHighlighter.ARGUMENT).create();
    }
}
