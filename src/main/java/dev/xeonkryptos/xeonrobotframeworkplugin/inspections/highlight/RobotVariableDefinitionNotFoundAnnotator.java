package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.highlight;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotPsiImplUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPythonExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.regex.Pattern;

public class RobotVariableDefinitionNotFoundAnnotator extends AbstractRobotVariableAnnotator {

    private static final Pattern NUMBERS_PATTERN = Pattern.compile("\\d+");

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (isEvaluatable(element)) {
            RobotVariable variable = (RobotVariable) element;
            RobotVariableBodyId variableBodyId = RobotPsiImplUtil.getVariableBodyId(variable);
            if (variableBodyId != null && Arrays.stream(((PsiPolyVariantReference) variableBodyId.getReference()).multiResolve(false))
                                                .filter(ResolveResult::isValidResult)
                                                .map(ResolveResult::getElement)
                                                .noneMatch(result -> result instanceof RobotVariableDefinition)) {
                RobotVariableAnalyser robotVariableAnalyser = new RobotVariableAnalyser();
                variable.accept(robotVariableAnalyser);

                String variableName = variable.getVariableName();
                assert variableName != null;

                if (!robotVariableAnalyser.variableDefinitionAsParent && !robotVariableAnalyser.pythonExpressionVariableBodyFound && !NUMBERS_PATTERN.matcher(
                        variableName).matches()) {
                    holder.newAnnotation(HighlightSeverity.WEAK_WARNING, RobotBundle.message("annotation.variable.not-found")).range(element).create();
                }
            }
        }
    }

    private static final class RobotVariableAnalyser extends RobotVisitor {

        private boolean variableDefinitionAsParent = false;
        private boolean pythonExpressionVariableBodyFound = false;

        @Override
        public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
            variableDefinitionAsParent = true;
        }

        @Override
        public void visitVariable(@NotNull RobotVariable o) {
            PsiElement parent = o.getParent();
            if (parent != null) {
                parent.accept(this);
            }
            o.acceptChildren(this);
        }

        @Override
        public void visitPythonExpression(@NotNull RobotPythonExpression o) {
            pythonExpressionVariableBodyFound = true;
        }
    }
}
