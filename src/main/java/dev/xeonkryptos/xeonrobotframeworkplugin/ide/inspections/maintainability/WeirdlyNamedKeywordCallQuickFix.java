package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.maintainability;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;

public class WeirdlyNamedKeywordCallQuickFix extends LocalQuickFixOnPsiElement {

    private final String newName;

    public WeirdlyNamedKeywordCallQuickFix(@NotNull RobotKeywordCallName element, String newName) {
        super(element);

        this.newName = newName;
    }

    @NotNull
    @Override
    @IntentionName
    public String getText() {
        return RobotBundle.getMessage("intention.family.weird-naming.text", newName);
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        RobotKeywordCallName newKeywordCallName = RobotElementGenerator.getInstance(project).createNewKeywordCallName(newName);
        if (newKeywordCallName != null) {
            startElement.replace(newKeywordCallName);
        }
    }

    @Override
    public boolean isAvailable(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        return startElement instanceof RobotKeywordCallName;
    }

    @NotNull
    @Override
    @IntentionFamilyName
    public String getFamilyName() {
        return RobotBundle.getMessage("intention.family.weird-naming.name");
    }
}
