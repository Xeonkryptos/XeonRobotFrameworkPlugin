package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.cleanup;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.SimpleRobotInspection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotImportStatementsCollector;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class RobotImportNotUsed extends SimpleRobotInspection {

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return RobotBundle.getMessage("INSP.NAME.import.unused");
    }

    @Override
    public final boolean skip(PsiElement element) {
        if (element.getContainingFile().getFileType() != RobotFeatureFileType.getInstance()) {
            // TODO: Workaround for now. Don't "analyze" imports of other robot files. To be able to do that a more sophisticated usage analysis is needed.
            return true;
        }
        if (element instanceof RobotResourceImportGlobalSetting importElem) {
            RobotPositionalArgument positionalArgument = importElem.getImportedFile();
            PsiElement resolvedElement = positionalArgument.getReference().resolve();
            if (resolvedElement instanceof RobotFile) {
                RobotImportStatementsCollector collector = new RobotImportStatementsCollector();
                resolvedElement.acceptChildren(collector);
                List<String> importIdentifiers = collector.getImportedFiles();

                String importText = positionalArgument.getText();
                int firstOccurrenceIndex = importIdentifiers.indexOf(importText);
                int lastOccurrenceIndex = importIdentifiers.lastIndexOf(importText);

                if (firstOccurrenceIndex != lastOccurrenceIndex && collector.getImportElements().indexOf(importElem) != firstOccurrenceIndex) {
                    return false;
                }

                Collection<PsiFile> filesFromInvokedKeywordsAndVariables = ((RobotFile) positionalArgument.getContainingFile()).getFilesFromInvokedKeywordsAndVariables();
                PsiFile importedFile = resolvedElement.getContainingFile();
                return filesFromInvokedKeywordsAndVariables.contains(importedFile);
            }
        }
        return true;
    }

    @Override
    public final String getMessage() {
        return RobotBundle.getMessage("INSP.import.unused");
    }

    @NotNull
    @Override
    protected final String getGroupNameKey() {
        return "INSP.GROUP.cleanup";
    }
}
