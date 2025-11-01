package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.compilation;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;

class RemoveMoreThanOnceDefinedLocalArgumentsSettingQuickFix extends PsiElementBaseIntentionAction {

    public RemoveMoreThanOnceDefinedLocalArgumentsSettingQuickFix() {
        setText(RobotBundle.message("intention.family.remove.text.illegal-arguments-setting"));
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        RobotLocalArgumentsSetting localArgumentsSetting = PsiTreeUtil.getParentOfType(element, RobotLocalArgumentsSetting.class);
        if (localArgumentsSetting != null) {
            PsiElement eolChild = localArgumentsSetting.getLastChild();
            PsiElement prevEolElement;
            PsiElement newEolElement;
            if (eolChild.getTextLength() > 1) {
                PsiElement prevSibling = localArgumentsSetting.getPrevSibling();
                while (prevSibling instanceof PsiWhiteSpace) {
                    prevSibling = prevSibling.getPrevSibling();
                }
                if (prevSibling != null && prevSibling.getLastChild() != null && prevSibling.getLastChild().getNode().getElementType() == RobotTypes.EOL) {
                    prevEolElement = prevSibling.getLastChild();
                    int newLineCount = prevEolElement.getTextLength() + eolChild.getTextLength() - 1;
                    newEolElement = RobotElementGenerator.getInstance(project).createEolElement(newLineCount);
                } else {
                    prevEolElement = null;
                    newEolElement = null;
                }
            } else {
                prevEolElement = null;
                newEolElement = null;
            }
            CodeStyleManager.getInstance(element.getProject()).performActionWithFormatterDisabled((Runnable) () -> {
                localArgumentsSetting.delete();
                if (newEolElement != null) {
                    prevEolElement.replace(newEolElement);
                }
            });
        }
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return PsiTreeUtil.getParentOfType(element, RobotLocalArgumentsSetting.class) != null;
    }

    @NotNull
    @Override
    @IntentionFamilyName
    public String getFamilyName() {
        return RobotBundle.message("intention.family.remove.name.illegal-arguments-setting");
    }
}
