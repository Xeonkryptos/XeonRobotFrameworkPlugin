package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import org.jetbrains.annotations.NotNull;

class RemoveMoreThanOnceDefinedKeywordOnlyMarkersQuickFix extends PsiElementBaseIntentionAction {

    public RemoveMoreThanOnceDefinedKeywordOnlyMarkersQuickFix() {
        setText(RobotBundle.message("intention.family.remove.text.illegal-arguments-setting.keyword-only-markers"));
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        RobotVariableDefinition variableDefinition = PsiTreeUtil.getParentOfType(element, RobotVariableDefinition.class);
        if (variableDefinition != null) {
            variableDefinition.delete();
        }
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if (PsiTreeUtil.getParentOfType(element, RobotLocalArgumentsSetting.class) != null) {
            RobotVariableDefinition variableDefinition = PsiTreeUtil.getParentOfType(element, RobotVariableDefinition.class);
            return variableDefinition != null && variableDefinition.getName() == null;
        }
        return false;
    }

    @NotNull
    @Override
    @IntentionFamilyName
    public String getFamilyName() {
        return RobotBundle.message("intention.family.remove.name.illegal-arguments-setting.keyword-only-markers");
    }
}
