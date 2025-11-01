package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.highlight;

import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotEnvironmentVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotFileManager;

import java.util.Collection;

abstract class AbstractRobotVariableAnnotator implements Annotator {

    protected boolean isEvaluatable(PsiElement element) {
        if (!(element instanceof RobotVariable variable) || element instanceof RobotEnvironmentVariable) {
            return false;
        }
        String variableName = variable.getVariableName();
        if (variableName == null) {
            return false;
        }
        Collection<DefinedVariable> globalVariables = RobotFileManager.getGlobalVariables(element.getProject());
        if (globalVariables.stream().anyMatch(globalVariable -> globalVariable.matches(variableName))) {
            return false;
        }
        RobotLocalArgumentsSetting localArgumentsSetting = PsiTreeUtil.getParentOfType(element, RobotLocalArgumentsSetting.class);
        return localArgumentsSetting == null;
    }
}
