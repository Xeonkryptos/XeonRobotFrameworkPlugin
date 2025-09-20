package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.cleanup;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingParameterMandatory;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingParameterOptional;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
        if (robotKeywordOnlyMarkerVisitor != null && robotKeywordOnlyMarkerVisitor.keywordOnlyMarker != null) {
            RobotVariableDefinition localKeywordOnlyMarker = robotKeywordOnlyMarkerVisitor.keywordOnlyMarker;
            int keywordOnlyTextOffset = localKeywordOnlyMarker.getTextOffset();
            if (robotKeywordOnlyMarkerVisitor.argumentParameters.stream().noneMatch(param -> keywordOnlyTextOffset < param.getTextOffset())) {
                problemsHolder.registerProblem(localKeywordOnlyMarker,
                                               RobotBundle.getMessage("INSP.keyword-only-marker.unused"),
                                               ProblemHighlightType.LIKE_UNUSED_SYMBOL,
                                               new RemoveUnusedKeywordOnlyMarkerQuickFix(localKeywordOnlyMarker));
            }
        }
    }

    private static class RobotKeywordOnlyMarkerVisitor extends RobotVisitor {

        private final Set<PsiElement> argumentParameters = ConcurrentHashMap.newKeySet();

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
        public void visitLocalArgumentsSettingParameter(@NotNull RobotLocalArgumentsSettingParameter o) {
            super.visitLocalArgumentsSettingParameter(o);
            RobotLocalArgumentsSettingParameterMandatory parameterMandatory = o.getLocalArgumentsSettingParameterMandatory();
            RobotLocalArgumentsSettingParameterOptional parameterOptional = o.getLocalArgumentsSettingParameterOptional();

            if (parameterMandatory != null && parameterMandatory.getVariableDefinition().getName() == null) {
                keywordOnlyMarker = parameterMandatory.getVariableDefinition();
            } else if (parameterMandatory != null) {
                argumentParameters.add(parameterMandatory);
            } else {
                argumentParameters.add(parameterOptional);
            }
        }
    }
}
