package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotHighlighter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
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
            String parameterName = parameter.getParameterName();
            boolean directMatchFound = availableParameters.stream().anyMatch(param -> param.matches(parameterName));
            if (!directMatchFound && availableParameters.stream().noneMatch(DefinedParameter::isKeywordContainer)) {
                convertParameterToArgumentVisually(holder, parameter);
            }
        } else if (PsiTreeUtil.getParentOfType(parameter, RobotLocalSetting.class) != null) {
            convertParameterToArgumentVisually(holder, parameter);
        }
    }

    private static void convertParameterToArgumentVisually(@NotNull AnnotationHolder holder, RobotParameter parameter) {
        TextRange parameterTextAttributeRange = parameter.getTextRange();
        RobotVariable variable = PsiTreeUtil.findChildOfType(parameter, RobotVariable.class);
        if (variable != null) {
            RobotParameterId parameterId = parameter.getParameterId();
            parameterTextAttributeRange = parameterId.getTextRange();
            int textOffset = variable.getTextOffset();
            parameterTextAttributeRange = new TextRange(parameterTextAttributeRange.getStartOffset(), textOffset);
        }
        holder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES).range(parameterTextAttributeRange).textAttributes(RobotHighlighter.ARGUMENT).create();
    }
}
