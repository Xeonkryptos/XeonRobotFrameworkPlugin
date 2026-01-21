package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.maintainability;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibrary;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotPyUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.VariableNameUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WeirdlyNamedElementsInspection extends LocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new RobotVisitor() {
            @Override
            @SuppressWarnings("UnstableApiUsage")
            public void visitKeywordCallName(@NotNull RobotKeywordCallName o) {
                String fullKeywordCallName = o.getText().trim();
                String callName = fullKeywordCallName;
                RobotKeywordCallLibrary keywordCallLibrary = o.getKeywordCallLibrary();
                if (keywordCallLibrary != null) {
                    int textLength = keywordCallLibrary.getKeywordCallLibraryName().getTextLength();
                    callName = callName.substring(textLength + 1);
                }
                final String finalizedCallName = callName;
                PsiElement resolvedElement = o.getReference().resolve();
                String expectedName = null;
                if (resolvedElement instanceof PyFunction pyFunction) {
                    Optional<String> decoratorDefinedNameOpt = RobotPyUtil.findCustomKeywordNameDecoratorExpression(pyFunction)
                                                                          .map(PyStringLiteralExpression::getStringValue);
                    if (decoratorDefinedNameOpt.isPresent()) {
                        expectedName = decoratorDefinedNameOpt.filter(literal -> !literal.trim().equalsIgnoreCase(finalizedCallName)).orElse(null);
                    } else {
                        expectedName = Optional.ofNullable(pyFunction.getName())
                                               .map(functionName -> KeywordUtil.getInstance(pyFunction.getProject()).functionToKeyword(functionName))
                                               .filter(name -> !name.trim().equalsIgnoreCase(finalizedCallName))
                                               .orElse(null);
                    }
                } else if (resolvedElement != null) {
                    RobotUserKeywordStatement userKeywordStatement = (RobotUserKeywordStatement) resolvedElement;
                    String userKeywordName = userKeywordStatement.getName().trim();
                    if (!userKeywordName.equalsIgnoreCase(finalizedCallName) && !userKeywordName.equalsIgnoreCase(fullKeywordCallName)) {
                        expectedName = userKeywordName;
                    }
                }
                if (expectedName != null) {
                    holder.registerProblem(o,
                                           RobotBundle.message("INSP.weird.naming", expectedName),
                                           ProblemHighlightType.WARNING,
                                           new WeirdlyNamedKeywordCallQuickFix(o, expectedName));
                }
            }

            @Override
            public void visitVariableBodyId(@NotNull RobotVariableBodyId o) {
                RobotVariable variable = PsiTreeUtil.getParentOfType(o, RobotVariable.class);
                if (variable != null && !(variable.getParent() instanceof RobotVariableDefinition)) {
                    PsiElement element = o.getReference().resolve();
                    if (element instanceof RobotVariableDefinition definition) {
                        String definedName = definition.getName();
                        int variableBodyTextOffset = o.getTextOffset();
                        int variableTextOffset = variable.getTextOffset() + 2; // account for variable start
                        if (variableBodyTextOffset != variableTextOffset) {
                            // only check the first body id of the variable
                            return;
                        }
                        String usedName = o.getText().trim();
                        if (definedName != null && !definedName.equalsIgnoreCase(usedName)) {
                            String definedBaseVariableName = VariableNameUtil.INSTANCE.computeBaseVariableName(definedName);
                            String usedBaseVariableName = VariableNameUtil.INSTANCE.computeBaseVariableName(usedName);
                            if (!definedBaseVariableName.trim().equalsIgnoreCase(usedBaseVariableName)) {
                                holder.registerProblem(variable,
                                                       RobotBundle.message("INSP.weird.naming", definedBaseVariableName),
                                                       ProblemHighlightType.WARNING,
                                                       new WeirdlyNamedVariableElementQuickFix(o, definedName));
                            }
                        }
                    }
                }
            }
        };
    }
}
