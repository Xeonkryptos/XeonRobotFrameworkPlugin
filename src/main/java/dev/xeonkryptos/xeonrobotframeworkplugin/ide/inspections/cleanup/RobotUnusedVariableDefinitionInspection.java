package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.cleanup;

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector;
import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RobotUnusedVariableDefinitionInspection extends LocalInspectionTool {

    private static final Key<RobotVariableUsageVisitor> VARIABLE_USAGE_VISITOR_KEY = Key.create("ROBOT_VARIABLE_USAGE_VISITOR");

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        RobotVariableUsageVisitor robotVariableUsageVisitor = new RobotVariableUsageVisitor();
        session.putUserData(VARIABLE_USAGE_VISITOR_KEY, robotVariableUsageVisitor);
        return robotVariableUsageVisitor;
    }

    @Override
    public void inspectionFinished(@NotNull LocalInspectionToolSession session, @NotNull ProblemsHolder problemsHolder) {
        RobotVariableUsageVisitor variableUsageVisitor = session.getUserData(VARIABLE_USAGE_VISITOR_KEY);
        if (variableUsageVisitor != null) {
            Iterator<RobotVariableDefinition> definitionsIterator = variableUsageVisitor.foundVariableDefinitions.iterator();
            while (definitionsIterator.hasNext()) {
                RobotVariableDefinition variableDefinition = definitionsIterator.next();
                Iterator<RobotVariable> variableIterator = variableUsageVisitor.foundVariableUsages.iterator();
                while (variableIterator.hasNext()) {
                    RobotVariable variable = variableIterator.next();
                    String variableName = variable.getVariableName();
                    if (variableDefinition.isInScope(variable) && variableDefinition.matches(variableName)) {
                        variableIterator.remove();
                        definitionsIterator.remove();
                        break;
                    }
                }
            }
            SearchScope scope = GlobalSearchScope.projectScope(problemsHolder.getProject());
            for (RobotVariableDefinition unusedVariableDefinition : variableUsageVisitor.foundVariableDefinitions) {
                @SuppressWarnings("UnstableApiUsage")
                boolean problemDetected = ReferencesSearch.search(unusedVariableDefinition, scope)
                                                          .allowParallelProcessing()
                                                          .mapping(ref -> PsiTreeUtil.getParentOfType(ref.getElement(),
                                                                                                      RobotVariable.class,
                                                                                                      RobotVariableDefinition.class))
                                                          .filtering(Objects::nonNull)
                                                          .filtering(element -> {
                                                              ReadWriteAccessDetector detector = ReadWriteAccessDetector.findDetector(element);
                                                              return detector != null && !detector.isDeclarationWriteAccess(element);
                                                          })
                                                          .findFirst() == null;
                if (problemDetected) {
                    problemsHolder.registerProblem(unusedVariableDefinition,
                                                   RobotBundle.message("INSP.variable.unused"),
                                                   ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                                                   new RemoveUnusedVariableDefinitionQuickFix(unusedVariableDefinition));
                }
            }
        }
    }

    @Override
    public boolean runForWholeFile() {
        return true;
    }

    private static class RobotVariableUsageVisitor extends RobotVisitor {

        private final Set<RobotVariableDefinition> foundVariableDefinitions = ConcurrentHashMap.newKeySet();
        private final Set<RobotVariable> foundVariableUsages = ConcurrentHashMap.newKeySet();

        @Override
        public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
            super.visitVariableDefinition(o);
            String name = o.getName();
            if (name != null) {
                foundVariableDefinitions.add(o);
            }
        }

        @Override
        public void visitVariable(@NotNull RobotVariable o) {
            super.visitVariable(o);
            String variableName = o.getVariableName();
            if (variableName != null) {
                ReadWriteAccessDetector detector = ReadWriteAccessDetector.findDetector(o);
                if (detector != null && detector.getExpressionAccess(o) == Access.Read) {
                    foundVariableUsages.add(o);
                }
            }
        }
    }
}
