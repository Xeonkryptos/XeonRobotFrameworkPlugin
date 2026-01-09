package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.highlight;

import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.inspections.RobotAnnotator;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotDictVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotListVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotScalarVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotFileManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

abstract class AbstractRobotVariableAnnotator extends RobotAnnotator {

    @Override
    public void visitDictVariable(@NotNull RobotDictVariable o) {
        if (isEvaluatable(o)) {
            evaluateAnnotation(o);
        }
    }

    @Override
    public void visitListVariable(@NotNull RobotListVariable o) {
        if (isEvaluatable(o)) {
            evaluateAnnotation(o);
        }
    }

    @Override
    public void visitScalarVariable(@NotNull RobotScalarVariable o) {
        if (isEvaluatable(o)) {
            evaluateAnnotation(o);
        }
    }

    private boolean isEvaluatable(RobotVariable variable) {
        String variableName = variable.getVariableName();
        if (variableName == null) {
            return false;
        }
        Collection<DefinedVariable> globalVariables = RobotFileManager.getGlobalVariables(variable.getProject());
        if (globalVariables.stream().anyMatch(globalVariable -> globalVariable.matches(variableName))) {
            return false;
        }
        RobotLocalArgumentsSetting localArgumentsSetting = PsiTreeUtil.getParentOfType(variable, RobotLocalArgumentsSetting.class);
        return localArgumentsSetting == null;
    }

    protected abstract void evaluateAnnotation(@NotNull RobotVariable variable);
}
