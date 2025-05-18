package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.cleanup;

import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinitionGroup;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.VariableDefinitionId;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class RemoveUnusedVariableDefinitionIntentAction extends BaseIntentionAction {

    private static final Set<String> SET_VARIABLE_KEYWORD_NAMES = Set.of("set variable",
                                                                         "set global variable",
                                                                         "set local variable",
                                                                         "set test variable",
                                                                         "set task variable",
                                                                         "set suite variable",
                                                                         "set variable if");
    private final VariableDefinitionId variableDefinitionId;

    public RemoveUnusedVariableDefinitionIntentAction(VariableDefinitionId variableDefinitionId) {
        this.variableDefinitionId = variableDefinitionId;

        setText(RobotBundle.getMessage("intention.family.remove.text.unused-variable"));
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return true;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        if (!variableDefinitionId.isValid()) {
            return;
        }

        VariableDefinitionGroup variableDefinitionGroup = PsiTreeUtil.getParentOfType(variableDefinitionId, VariableDefinitionGroup.class);
        assert variableDefinitionGroup != null;
        boolean moreThanOneVariable = variableDefinitionGroup.hasMoreThanOneVariable();

        KeywordStatement keywordStatement = PsiTreeUtil.getChildOfType(variableDefinitionGroup, KeywordStatement.class);
        VariableDefinition variableDefinition = PsiTreeUtil.getParentOfType(variableDefinitionId, VariableDefinition.class);
        if (keywordStatement != null) {
            String keywordStatementName = keywordStatement.getName().toLowerCase();
            // To avoid side effects and keep it as simple as possible for the beginning, check if a valid keyword is used/gets called and this keyword isn't
            // simply setting a value into a variable. Then, we should only remove the variable, but not the keyword itself.
            // Also, we have to consider the case that a "keyword" isn't a real keyword. It could be rather an argument identified as a keyword but "converted"
            // to an argument. Handle those cases as setting only a variable.
            if (SET_VARIABLE_KEYWORD_NAMES.contains(keywordStatementName) || keywordStatement.getInvokable().getReference().resolve() == null) {
                removeEverything(variableDefinitionGroup);
            } else {
                assert variableDefinition != null;
                if (moreThanOneVariable) {
                    removeOneVariableOnly(variableDefinition, variableDefinitionGroup);
                } else {
                    replaceGroupWithKeyword(variableDefinitionGroup, keywordStatement);
                }
            }
        } else {
            removeEverything(variableDefinitionGroup);
        }
    }

    private void removeEverything(VariableDefinitionGroup variableDefinitionGroup) {
        variableDefinitionGroup.delete();
    }

    private void removeOneVariableOnly(VariableDefinition variableDefinition, VariableDefinitionGroup variableDefinitionGroup) {
        PsiElement firstChild = variableDefinitionGroup.getFirstChild();
        VariableDefinition prevVariableDefinition = PsiTreeUtil.getPrevSiblingOfType(variableDefinition, VariableDefinition.class);
        if (firstChild != variableDefinition
            && PsiTreeUtil.getParentOfType(prevVariableDefinition, VariableDefinitionGroup.class) == variableDefinitionGroup) {
            // Removing whitespace to the previous variable definition
            PsiElement nextSibling = prevVariableDefinition.getNextSibling();
            variableDefinitionGroup.deleteChildRange(nextSibling, variableDefinition);
        } else {
            VariableDefinition nextVariableDefinition = PsiTreeUtil.getNextSiblingOfType(variableDefinition, VariableDefinition.class);
            if (nextVariableDefinition != null
                && PsiTreeUtil.getParentOfType(prevVariableDefinition, VariableDefinitionGroup.class) == variableDefinitionGroup) {
                // Removing whitespace to the next variable definition
                PsiElement prevSibling = nextVariableDefinition.getPrevSibling();
                variableDefinitionGroup.deleteChildRange(variableDefinition, prevSibling);
            } else {
                variableDefinition.delete();
            }
        }
    }

    private void replaceGroupWithKeyword(VariableDefinitionGroup variableDefinitionGroup, KeywordStatement keywordStatement) {
        variableDefinitionGroup.replace(keywordStatement);
    }

    @NotNull
    @IntentionFamilyName
    @Override
    public String getFamilyName() {
        return RobotBundle.getMessage("intention.family.remove.name.unused");
    }
}
