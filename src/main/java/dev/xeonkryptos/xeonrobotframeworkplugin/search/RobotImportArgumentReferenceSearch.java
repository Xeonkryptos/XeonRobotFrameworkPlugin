package dev.xeonkryptos.xeonrobotframeworkplugin.search;

import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch.SearchParameters;
import com.intellij.util.Processor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.ImportArgumentIndex;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

public class RobotImportArgumentReferenceSearch extends QueryExecutorBase<PsiReference, SearchParameters> {

    public RobotImportArgumentReferenceSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
        PsiElement element = queryParameters.getElementToSearch();
        Project project = queryParameters.getProject();

        GlobalSearchScope globalSearchScope = QueryExecutorUtil.convertToGlobalSearchScope(queryParameters.getEffectiveSearchScope(), project);
        Optional<ModuleRootManager> moduleRootManagerOpt = Optional.of(element)
                                                                   .filter(elem -> elem instanceof RobotFile)
                                                                   .map(elem -> ((RobotFile) elem).getVirtualFile())
                                                                   .filter(VirtualFile::isInLocalFileSystem)
                                                                   .map(vFile -> ProjectFileIndex.getInstance(project).getModuleForFile(vFile))
                                                                   .map(ModuleRootManager::getInstance);
        if (moduleRootManagerOpt.isPresent()) {
            VirtualFile virtualFile = ((RobotFile) element).getVirtualFile();
            ModuleRootManager moduleRootManager = moduleRootManagerOpt.get();
            VirtualFile[] contentRoots = moduleRootManager.getContentRoots();
            if (contentRoots.length > 0) {
                String relativePath;
                int index = 0;
                do {
                    relativePath = VfsUtilCore.getRelativePath(virtualFile, contentRoots[index], '.');
                    ++index;
                } while (relativePath == null && index < contentRoots.length);

                if (relativePath != null) {
                    ImportArgumentIndex importArgumentIndex = ImportArgumentIndex.getInstance();
                    Collection<RobotImportArgument> matchingArguments = importArgumentIndex.getImportArgument(relativePath, project, globalSearchScope);
                    for (RobotImportArgument argument : matchingArguments) {
                        if (!consumer.process(new PsiReferenceBase.Immediate<>(argument, element))) {
                            return;
                        }
                    }
                }
            }
        }
    }
}
