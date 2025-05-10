package dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.cleanup;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.inspections.SimpleRobotInspection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotFeatureFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Import;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.PositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
            // TODO: Workaround for now. Don't "analyze" imports in Resource files. To be able to do that a more sophisticated usage analysis is needed.
            return true;
        }
        if (element instanceof PositionalArgument positionalArgument) {
            PsiElement parentElement = positionalArgument.getParent();
            if (parentElement instanceof Import importElem && importElem.isResource()) {
                PsiElement resolvedElement = positionalArgument.getReference().resolve();
                if (resolvedElement instanceof RobotFile) {
                    Collection<Import> importElements = PsiTreeUtil.findChildrenOfType(positionalArgument.getContainingFile(), Import.class);
                    List<String> importIdentifiers = new ArrayList<>(importElements.size());

                    for (Import importElement : importElements) {
                        String importText = importElement.getImportText();
                        importIdentifiers.add(importText);
                    }

                    String importText = importElem.getImportText();
                    int firstOccurrenceIndex = importIdentifiers.indexOf(importText);
                    int lastOccurrenceIndex = importIdentifiers.lastIndexOf(importText);

                    List<Import> importsCopy = new ArrayList<>(importElements);
                    if (firstOccurrenceIndex != lastOccurrenceIndex && importsCopy.indexOf(parentElement) != firstOccurrenceIndex) {
                        return false;
                    }

                    Collection<PsiFile> filesFromInvokedKeywordsAndVariables = ((RobotFile) positionalArgument.getContainingFile()).getFilesFromInvokedKeywordsAndVariables();
                    PsiFile importedFile = resolvedElement.getContainingFile();
                    return filesFromInvokedKeywordsAndVariables.contains(importedFile);
                }
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
