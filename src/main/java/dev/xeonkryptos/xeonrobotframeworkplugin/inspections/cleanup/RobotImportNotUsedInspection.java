package dev.xeonkryptos.xeonrobotframeworkplugin.inspections.cleanup;

import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotImportStatementsCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class RobotImportNotUsedInspection extends LocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session) {
        return new RobotVisitor() {
            @Override
            public void visitResourceImportGlobalSetting(@NotNull RobotResourceImportGlobalSetting o) {
                super.visitResourceImportGlobalSetting(o);
                RobotImportArgument importArgument = o.getImportedFile();
                if (importArgument == null) {
                    return;
                }
                PsiElement resolvedElement = importArgument.getReference().resolve();
                if (resolvedElement instanceof RobotFile) {
                    RobotImportStatementsCollector collector = new RobotImportStatementsCollector();
                    resolvedElement.acceptChildren(collector);
                    List<String> importIdentifiers = collector.getImportedFiles();

                    String importText = importArgument.getText();
                    int firstOccurrenceIndex = importIdentifiers.indexOf(importText);
                    int lastOccurrenceIndex = importIdentifiers.lastIndexOf(importText);

                    if (firstOccurrenceIndex != lastOccurrenceIndex && collector.getImportElements().indexOf(o) != firstOccurrenceIndex) {
                        holder.registerProblem(o, RobotBundle.message("INSP.import.unused"), ProblemHighlightType.LIKE_UNUSED_SYMBOL);
                    } else {
                        Collection<PsiFile> filesFromInvokedKeywordsAndVariables = ((RobotFile) importArgument.getContainingFile()).getFilesFromInvokedKeywordsAndVariables();
                        PsiFile importedFile = resolvedElement.getContainingFile();
                        if (!filesFromInvokedKeywordsAndVariables.contains(importedFile)) {
                            holder.registerProblem(o, RobotBundle.message("INSP.import.unused"), ProblemHighlightType.LIKE_UNUSED_SYMBOL);
                        }
                    }
                }
            }
        };
    }

    @Override
    public boolean runForWholeFile() {
        return true;
    }
}
