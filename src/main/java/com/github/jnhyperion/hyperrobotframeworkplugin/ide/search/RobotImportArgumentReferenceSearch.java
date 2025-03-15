package com.github.jnhyperion.hyperrobotframeworkplugin.ide.search;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.PositionalArgument;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.stub.index.PositionalArgumentImportIndex;
import com.intellij.openapi.application.QueryExecutorBase;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch.SearchParameters;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class RobotImportArgumentReferenceSearch extends QueryExecutorBase<PsiReference, SearchParameters> {

    public RobotImportArgumentReferenceSearch() {
        super(true);
    }

    @Override
    public void processQuery(@NotNull SearchParameters queryParameters, @NotNull Processor<? super PsiReference> consumer) {
        PsiElement element = queryParameters.getElementToSearch();
        Project project = queryParameters.getProject();

        GlobalSearchScope globalSearchScope = QueryExecutorUtil.convertToGlobalSearchScope(queryParameters.getEffectiveSearchScope(), project);
        if (element instanceof RobotFile robotFile) {
            VirtualFile virtualFile = robotFile.getVirtualFile();
            if (virtualFile.isInLocalFileSystem()) {
                Module moduleForFile = ProjectFileIndex.getInstance(project).getModuleForFile(virtualFile);
                if (moduleForFile != null) {
                    ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(moduleForFile);
                    VirtualFile[] contentRoots = moduleRootManager.getContentRoots();
                    if (contentRoots.length > 0) {
                        String relativePath;
                        int index = 0;
                        do {
                            relativePath = VfsUtilCore.getRelativePath(virtualFile, contentRoots[index], '.');
                            ++index;
                        } while (relativePath == null && index < contentRoots.length);
                        if (relativePath != null) {
                            Collection<PositionalArgument> matchingArguments = PositionalArgumentImportIndex.getInstance()
                                                                                                            .getPositionalArgumentForImport(relativePath,
                                                                                                                                            project,
                                                                                                                                            globalSearchScope);
                            for (PositionalArgument argument : matchingArguments) {
                                if (argument != null && !consumer.process(argument.getReference())) {
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
