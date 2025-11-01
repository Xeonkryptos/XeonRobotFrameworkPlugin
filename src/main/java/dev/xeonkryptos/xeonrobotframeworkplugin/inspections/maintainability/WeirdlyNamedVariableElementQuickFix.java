package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;

public class WeirdlyNamedVariableElementQuickFix extends LocalQuickFixOnPsiElement {

    private final String newName;

    public WeirdlyNamedVariableElementQuickFix(@NotNull RobotVariableBodyId element, String newName) {
        super(element);
        
        this.newName = newName;
    }

    @NotNull
    @Override
    @IntentionName
    public String getText() {
        return RobotBundle.message("intention.family.weird-naming.text", newName);
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        RobotVariableBodyId newVariableBodyId = RobotElementGenerator.getInstance(project).createNewVariableBodyId(newName);
        if (newVariableBodyId != null) {
            startElement.replace(newVariableBodyId);
        }
    }

    @Override
    public boolean isAvailable(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        return startElement instanceof RobotVariableBodyId;
    }

    @NotNull
    @Override
    @IntentionFamilyName
    public String getFamilyName() {
        return RobotBundle.message("intention.family.weird-naming.name");
    }
}
