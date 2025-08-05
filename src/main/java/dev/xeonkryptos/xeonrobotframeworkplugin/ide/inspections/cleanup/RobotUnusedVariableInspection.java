package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.cleanup;

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector;
import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElementVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RobotUnusedVariableInspection extends LocalInspectionTool {

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
            for (RobotVariableDefinition unusedVariableDefinition : variableUsageVisitor.unusedVariables.values()) {
                problemsHolder.registerProblem(unusedVariableDefinition,
                                               RobotBundle.getMessage("INSP.variable.unused"),
                                               ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                                               new RemoveUnusedVariableDefinitionQuickFix(unusedVariableDefinition));
            }
        }
    }

    @Override
    public boolean runForWholeFile() {
        return true;
    }

    private static class RobotVariableUsageVisitor extends RobotVisitor {

        private final Set<String> definedVariableNames = ConcurrentHashMap.newKeySet();
        private final Map<String, RobotVariableDefinition> unusedVariables = new ConcurrentHashMap<>();

        @Override
        public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
            super.visitVariableDefinition(o);
            String name = o.getName();
            if (name != null && definedVariableNames.add(name)) {
                unusedVariables.put(name, o);
            }
        }

        @Override
        public void visitVariable(@NotNull RobotVariable o) {
            super.visitVariable(o);
            String variableName = o.getVariableName();
            if (variableName != null) {
                ReadWriteAccessDetector detector = ReadWriteAccessDetector.findDetector(o);
                if (detector != null && detector.getExpressionAccess(o) == Access.Read) {
                    unusedVariables.remove(variableName);
                }
            }
        }
    }
}
