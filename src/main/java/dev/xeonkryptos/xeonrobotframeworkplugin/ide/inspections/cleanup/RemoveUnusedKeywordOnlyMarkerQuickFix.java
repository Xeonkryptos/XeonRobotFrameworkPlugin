package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.cleanup;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import org.jetbrains.annotations.NotNull;

class RemoveUnusedKeywordOnlyMarkerQuickFix extends LocalQuickFixOnPsiElement {

    public RemoveUnusedKeywordOnlyMarkerQuickFix(@NotNull PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    @IntentionName
    public String getText() {
        return RobotBundle.getMessage("intention.family.remove.text.unused-keyword-only-marker");
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        startElement.delete();
    }

    @NotNull
    @Override
    @IntentionFamilyName
    public String getFamilyName() {
        return RobotBundle.getMessage("intention.family.remove.name.unused");
    }
}
