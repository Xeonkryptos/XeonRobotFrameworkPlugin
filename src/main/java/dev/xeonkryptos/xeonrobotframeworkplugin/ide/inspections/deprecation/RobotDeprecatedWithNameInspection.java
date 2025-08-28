package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.deprecation;

import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.RobotVersionBasedInspection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotVersionProvider.RobotVersion;
import org.jetbrains.annotations.NotNull;

public class RobotDeprecatedWithNameInspection extends RobotVersionBasedInspection implements DumbAware {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new RobotVisitor() {
            @Override
            public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
                super.visitLibraryImportGlobalSetting(o);
                PsiElement libraryNameElement = o.getLibraryNameElement();
                RobotVersion robotVersion = getRobotVersion(session);
                if (libraryNameElement != null && "WITH_NAME".equalsIgnoreCase(libraryNameElement.getText()) && robotVersion.supports(new RobotVersion(6,
                                                                                                                                                       0,
                                                                                                                                                       0))) {
                    holder.registerProblem(libraryNameElement,
                                           RobotBundle.getMessage("INSP.library.import.with-name.deprecated"),
                                           ProblemHighlightType.LIKE_DEPRECATED);
                }
            }
        };
    }
}
