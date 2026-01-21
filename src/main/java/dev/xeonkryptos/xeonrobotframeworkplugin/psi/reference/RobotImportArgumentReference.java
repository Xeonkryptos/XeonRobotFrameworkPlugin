package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotResourceFileType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesImportGlobalSetting;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

public class RobotImportArgumentReference extends PsiPolyVariantReferenceBase<RobotImportArgument> {

    public RobotImportArgumentReference(@NotNull RobotImportArgument importArgument) {
        super(importArgument);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        return ResolveCache.getInstance(getElement().getProject()).resolveWithCaching(this, (resolver, incompCode) -> multiResolve(getElement()), false, incompleteCode);
    }

    private static ResolveResult @NotNull [] multiResolve(RobotImportArgument importArgument) {
        Project project = importArgument.getProject();
        PsiElement parent = importArgument.getParent();
        String argumentValue = importArgument.getText();

        Set<ResolveResult> results = new LinkedHashSet<>();
        if (parent instanceof RobotResourceImportGlobalSetting resourceImport) {
            PsiFile containingFile = resourceImport.getContainingFile();
            PsiFile resourceFile = ResourceFileImportFinder.getInstance(project).findFileInFileSystem(argumentValue, containingFile, RobotResourceFileType.getInstance());
            if (resourceFile != null) {
                results.add(new PsiElementResolveResult(resourceFile));
            }
        } else if (parent instanceof RobotLibraryImportGlobalSetting || parent instanceof RobotVariablesImportGlobalSetting) {
            PsiFile containingFile = parent.getContainingFile();
            PsiFile resourceFile = ResourceFileImportFinder.getInstance(project).findFileInFileSystem(argumentValue, containingFile);
            if (resourceFile != null) {
                results.add(new PsiElementResolveResult(resourceFile));
            } else {
                // File not directly found in file system. Try to find it in module search path (e.g. for classes or modules)
                PsiElement result = PythonResolver.resolveElement(argumentValue, project);
                if (result != null) {
                    results.add(new PsiElementResolveResult(result));
                }
            }
        }
        return results.toArray(ResolveResult.EMPTY_ARRAY);
    }
}
