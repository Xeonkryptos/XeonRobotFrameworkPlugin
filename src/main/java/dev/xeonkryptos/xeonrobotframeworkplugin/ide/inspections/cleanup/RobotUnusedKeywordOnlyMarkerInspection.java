package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.cleanup;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElementVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class RobotUnusedKeywordOnlyMarkerInspection extends LocalInspectionTool implements DumbAware {

    private static final Key<RobotKeywordOnlyMarkerVisitor> KEYWORD_ONLY_USAGE_MARKER_VISITOR_KEY = Key.create("KEYWORD_ONLY_USAGE_MARKER_VISITOR");

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        RobotKeywordOnlyMarkerVisitor robotKeywordOnlyMarkerVisitor = new RobotKeywordOnlyMarkerVisitor();
        session.putUserData(KEYWORD_ONLY_USAGE_MARKER_VISITOR_KEY, robotKeywordOnlyMarkerVisitor);
        return robotKeywordOnlyMarkerVisitor;
    }

    @Override
    public void inspectionFinished(@NotNull LocalInspectionToolSession session, @NotNull ProblemsHolder problemsHolder) {
        RobotKeywordOnlyMarkerVisitor robotKeywordOnlyMarkerVisitor = session.getUserData(KEYWORD_ONLY_USAGE_MARKER_VISITOR_KEY);
        if (robotKeywordOnlyMarkerVisitor != null) {
            if (robotKeywordOnlyMarkerVisitor.keywordOnlyMarker != null && !robotKeywordOnlyMarkerVisitor.argumentWithDefaultValueFound.get()) {
                problemsHolder.registerProblem(robotKeywordOnlyMarkerVisitor.keywordOnlyMarker,
                                               RobotBundle.getMessage("INSP.keyword-only-marker.unused"),
                                               ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                                               new RemoveUnusedKeywordOnlyMarkerQuickFix(robotKeywordOnlyMarkerVisitor.keywordOnlyMarker));
            }
        }
    }

    private static class RobotKeywordOnlyMarkerVisitor extends RobotVisitor {

        private final AtomicBoolean argumentWithDefaultValueFound = new AtomicBoolean();

        private volatile RobotVariableDefinition keywordOnlyMarker;

        @Override
        public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
            super.visitVariableDefinition(o);
            String name = o.getName();
            if (name == null && o.getParent() instanceof RobotLocalArgumentsSetting) {
                keywordOnlyMarker = o;
            }
        }

        @Override
        public void visitLocalArgumentsSettingArgument(@NotNull RobotLocalArgumentsSettingArgument o) {
            super.visitLocalArgumentsSettingArgument(o);
            if (keywordOnlyMarker != null) {
                argumentWithDefaultValueFound.set(true);
            }
        }
    }
}
