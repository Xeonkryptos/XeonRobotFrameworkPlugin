package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotHighlighter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameterId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RobotParameterAnnotator implements Annotator {

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RobotParameter parameter)) {
            return;
        }
        RobotKeywordCall keywordStatement = PsiTreeUtil.getParentOfType(parameter, RobotKeywordCall.class);
        if (keywordStatement != null) {
            Collection<DefinedParameter> availableParameters = keywordStatement.getAvailableParameters();
            String parameterName = parameter.getName();
            boolean directMatchFound = availableParameters.stream().anyMatch(param -> param.matches(parameterName));
            if (!directMatchFound && availableParameters.stream().noneMatch(DefinedParameter::isKeywordContainer)) {
                convertParameterToArgumentVisually(holder, parameter);
            }
        }

        RobotParameterId parameterId = parameter.getNameIdentifier();
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION).range(parameterId).textAttributes(DefaultLanguageHighlighterColors.PARAMETER).create();
    }

    private static void convertParameterToArgumentVisually(@NotNull AnnotationHolder holder, RobotParameter parameter) {
        TextRange parameterTextAttributeRange = parameter.getTextRange();
        RobotVariable variable = PsiTreeUtil.findChildOfType(parameter, RobotVariable.class);
        if (variable != null) {
            PsiElement nameIdentifier = parameter.getNameIdentifier();
            parameterTextAttributeRange = nameIdentifier.getTextRange();
            int textOffset = variable.getTextOffset();
            parameterTextAttributeRange = new TextRange(parameterTextAttributeRange.getStartOffset(), textOffset);
        }
        holder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).range(parameterTextAttributeRange).textAttributes(RobotHighlighter.ARGUMENT).create();
    }
}
