package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotHighlighter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotEnvironmentVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPythonExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotFileManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
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
        Collection<DefinedVariable> globalVariables = RobotFileManager.getGlobalVariables(element.getProject());
        if (globalVariables.stream().anyMatch(globalVariable -> globalVariable.matches(variableName))) {
            return;
        }
        RobotLocalSetting localSetting = PsiTreeUtil.getParentOfType(element, RobotLocalSetting.class);
        if (localSetting != null && "[Arguments]".equalsIgnoreCase(localSetting.getName())) {
            return;
        }

        RobotVariableBodyId nameIdentifier = variable.getNameIdentifier();
        if (nameIdentifier != null && ((PsiPolyVariantReference) nameIdentifier.getReference()).multiResolve(false).length > 0) {
            ResolveResult[] resolveResults = ((PsiPolyVariantReference) nameIdentifier.getReference()).multiResolve(false);
            if (resolveResults.length > 0) {
                if (resolveResults.length > 1) {
                    holder.newSilentAnnotation(HighlightSeverity.TEXT_ATTRIBUTES)
                          .textAttributes(RobotHighlighter.REASSIGNED_VARIABLE)
                          .tooltip(RobotBundle.getMessage("annotation.variable.reassigned"))
                          .range(element)
                          .create();
                }
                return;
            }
        }
        RobotVariableAnalyser robotVariableAnalyser = new RobotVariableAnalyser();
        variable.accept(robotVariableAnalyser);
        if (!robotVariableAnalyser.variableDefinitionAsParent && !robotVariableAnalyser.pythonExpressionVariableBodyFound) {
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, RobotBundle.getMessage("annotation.variable.not-found")).range(element).create();
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
