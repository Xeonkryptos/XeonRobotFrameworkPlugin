package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.compilation;

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiParserFacade;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

class InsertMissingMandatoryKeywordParametersQuickFix extends PsiElementBaseIntentionAction {

    private final Collection<String> missingRequiredParameters;

    public InsertMissingMandatoryKeywordParametersQuickFix(Collection<String> missingRequiredParameters) {
        this.missingRequiredParameters = missingRequiredParameters;
        setText(RobotBundle.getMessage("intention.family.insert.text.keyword.missing-required-parameters"));
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        RobotElementGenerator robotElementGenerator = RobotElementGenerator.getInstance(project);
        RobotKeywordCall keywordCall = PsiTreeUtil.getParentOfType(element, RobotKeywordCall.class, false);
        assert keywordCall != null;

        RobotKeywordCallName keywordCallName = keywordCall.getKeywordCallName();
        PsiElement whiteSpace = project.getService(PsiParserFacade.class).createWhiteSpaceFromText("  ");
        for (String missingRequiredParameter : missingRequiredParameters) {
            RobotParameter newParameter = robotElementGenerator.createNewParameter(missingRequiredParameter);
            if (newParameter != null) {
                keywordCall.addAfter(newParameter, keywordCallName);
                keywordCall.addAfter(whiteSpace, keywordCallName);
            }
        }
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        return PsiTreeUtil.getParentOfType(element, RobotKeywordCall.class, false) != null;
    }

    @NotNull
    @Override
    @IntentionFamilyName
    public String getFamilyName() {
        return RobotBundle.getMessage("intention.family.insert.name.keyword.missing-required-parameters");
    }
}
