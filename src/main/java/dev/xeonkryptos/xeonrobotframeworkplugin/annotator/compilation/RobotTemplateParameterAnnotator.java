package dev.xeonkryptos.xeonrobotframeworkplugin.annotator.compilation;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.annotator.RobotAnnotator;
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotHighlighter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateArguments;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RobotTemplateParameterAnnotator extends RobotAnnotator {

    @Override
    public void visitTemplateParameter(@NotNull RobotTemplateParameter parameter) {
        RobotKeywordCall keywordCall = KeywordUtil.findTemplateKeywordCall(parameter);
        if (keywordCall != null) {
            Collection<DefinedParameter> availableParameters = keywordCall.getAvailableParameters();
            String parameterName = parameter.getParameterName();
            boolean directMatchFound = availableParameters.stream().anyMatch(param -> param.matches(parameterName));
            if (!directMatchFound && availableParameters.stream().noneMatch(DefinedParameter::isKeywordContainer)) {
                keywordCall.getStartOfKeywordsOnlyIndex()
                           .ifPresentOrElse(keywordsOnlyIndex -> handleParameterWithinKeywordOnlyKeywordCall(keywordsOnlyIndex,
                                                                                                             parameter,
                                                                                                             keywordCall.getName(),
                                                                                                             getHolder()),
                                            () -> convertParameterToArgumentVisually(getHolder(), parameter));
            }
        }
    }

    private static void handleParameterWithinKeywordOnlyKeywordCall(int keywordsOnlyIndex,
                                                                    RobotTemplateParameter parameter,
                                                                    String keywordName,
                                                                    @NotNull AnnotationHolder holder) {
        if (keywordsOnlyIndex == 0) {
            highlightParameterAsNotFound(parameter, keywordName, holder);
        } else {
            RobotTemplateArguments templateArguments = PsiTreeUtil.getParentOfType(parameter, RobotTemplateArguments.class, true);
            assert templateArguments != null;
            Collection<RobotArgument> allTemplateArguments = templateArguments.getAllCallArguments();

            int index = 0;
            for (RobotArgument argument : allTemplateArguments) {
                if (argument == parameter) {
                    break;
                }
                index++;
            }

            if (index >= keywordsOnlyIndex) {
                highlightParameterAsNotFound(parameter, keywordName, holder);
            } else {
                convertParameterToArgumentVisually(holder, parameter);
            }
        }
    }

    private static void highlightParameterAsNotFound(RobotTemplateParameter parameter, String keywordName, AnnotationHolder holder) {
        holder.newAnnotation(HighlightSeverity.WARNING, RobotBundle.message("annotation.keyword.parameter.not-found", keywordName))
              .highlightType(ProblemHighlightType.WARNING)
              .range(parameter.getTemplateParameterId())
              .create();
    }

    private static void convertParameterToArgumentVisually(@NotNull AnnotationHolder holder, RobotTemplateParameter parameter) {
        // Grow range by 1 to include the assignment operator
        TextRange parameterIdTextRange = parameter.getTemplateParameterId().getTextRange().grown(1);
        holder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).range(parameterIdTextRange).textAttributes(RobotHighlighter.ARGUMENT).create();
    }
}
