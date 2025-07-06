package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotEnvironmentVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class RobotVariableAnnotator implements Annotator, DumbAware {

    private static final Pattern NUMBERS_PATTERN = Pattern.compile("\\d+");

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof RobotVariable variable) || element instanceof RobotEnvironmentVariable) {
            return;
        }
        String variableName = variable.getName();
        if (variableName == null || variableName.isBlank() || NUMBERS_PATTERN.matcher(variableName).matches()) {
            return;
        }

        PsiReference reference = variable.getReference();
        if (reference.resolve() != null) {
            return;
        }
        if (!(variable.getParent() instanceof RobotVariableDefinition)) {
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, RobotBundle.getMessage("annotation.variable.not-found")).range(element).create();
        }
    }
}
