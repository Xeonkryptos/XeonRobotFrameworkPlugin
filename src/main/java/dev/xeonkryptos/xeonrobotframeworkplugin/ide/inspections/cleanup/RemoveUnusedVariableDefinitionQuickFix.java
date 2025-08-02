package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.cleanup;

import com.intellij.codeInspection.LocalQuickFixOnPsiElement;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.misc.RobotReadWriteAccessDetector;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class RemoveUnusedVariableDefinitionQuickFix extends LocalQuickFixOnPsiElement {

    public RemoveUnusedVariableDefinitionQuickFix(@NotNull PsiElement element) {
        super(element);
    }

    @NotNull
    @Override
    @IntentionName
    public String getText() {
        return RobotBundle.getMessage("intention.family.remove.text.unused-variable");
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiFile file, @NotNull PsiElement startElement, @NotNull PsiElement endElement) {
        RobotVariableDefinition variableDefinition = (RobotVariableDefinition) startElement;
        RobotKeywordVariableStatement robotKeywordVariableStatement = PsiTreeUtil.getParentOfType(variableDefinition, RobotKeywordVariableStatement.class);

        if (robotKeywordVariableStatement != null) {
            List<RobotVariableDefinition> variableDefinitionList = robotKeywordVariableStatement.getVariableDefinitionList();
            RobotKeywordCall keywordCall = robotKeywordVariableStatement.getKeywordCall();
            boolean moreThanOneVariable = variableDefinitionList.size() > 1;

            String keywordStatementName = keywordCall.getName();
            // To avoid side effects and keep it as simple as possible for the beginning, check if a valid keyword is used/gets called and this keyword isn't
            // simply setting a value into a variable. Then, we should only remove the variable, but not the keyword itself.
            // Also, we have to consider the case that a "keyword" isn't a real keyword. It could be rather an argument identified as a keyword but "converted"
            // to an argument. Handle those cases as setting only a variable.
            if (RobotReadWriteAccessDetector.isVariableSetterKeyword(keywordStatementName)
                || keywordCall.getKeywordCallName().getReference().resolve() == null) {
                removeEverything(robotKeywordVariableStatement);
            } else {
                if (moreThanOneVariable) {
                    removeOneVariableOnly(variableDefinition, robotKeywordVariableStatement);
                } else {
                    replaceGroupWithKeyword(robotKeywordVariableStatement, keywordCall);
                }
            }
        } else {
            PsiElement parent = variableDefinition.getParent();
            removeEverything(parent);
        }
    }

    private void removeEverything(PsiElement variableDefinitionGroup) {
        variableDefinitionGroup.delete();
    }

    private void removeOneVariableOnly(RobotVariableDefinition variableDefinition, RobotKeywordVariableStatement keywordVariableStatement) {
        List<RobotVariableDefinition> variableDefinitionList = keywordVariableStatement.getVariableDefinitionList();
        Set<RobotVariableDefinition> variableDefinitionSet = Set.copyOf(variableDefinitionList);
        PsiElement firstChild = variableDefinitionList.getFirst();
        RobotVariableDefinition prevVariableDefinition = PsiTreeUtil.getPrevSiblingOfType(variableDefinition, RobotVariableDefinition.class);
        if (firstChild != variableDefinition && prevVariableDefinition != null && variableDefinitionSet.contains(prevVariableDefinition)) {
            // Removing whitespace to the previous variable definition
            PsiElement nextSibling = prevVariableDefinition.getNextSibling();
            keywordVariableStatement.deleteChildRange(nextSibling, variableDefinition);
        } else {
            RobotVariableDefinition nextVariableDefinition = PsiTreeUtil.getNextSiblingOfType(variableDefinition, RobotVariableDefinition.class);
            if (nextVariableDefinition != null && variableDefinitionSet.contains(prevVariableDefinition)) {
                // Removing whitespace to the next variable definition
                PsiElement prevSibling = nextVariableDefinition.getPrevSibling();
                keywordVariableStatement.deleteChildRange(variableDefinition, prevSibling);
            } else {
                variableDefinition.delete();
            }
        }
    }

    private void replaceGroupWithKeyword(RobotKeywordVariableStatement keywordVariableStatement, RobotKeywordCall keywordCall) {
        keywordVariableStatement.replace(keywordCall);
    }

    @NotNull
    @IntentionFamilyName
    @Override
    public String getFamilyName() {
        return RobotBundle.getMessage("intention.family.remove.name.unused");
    }
}
