package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.VariableDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedVariable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.ReservedVariable;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.QualifiedName;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.search.PySearchUtilBase;
import com.jetbrains.python.psi.stubs.PyModuleNameIndex;
import org.apache.commons.lang.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobotFileManager {

    private RobotFileManager() {
    }

    public static synchronized void clearProjectCache(Project var0) {
        ProjectFileCache.getCachedElements(var0).clear();
        ProjectFileCache.getCachedFiles(var0).clear();
        ProjectFileCache.getGlobalVariables(var0).clear();
        ProjectFileCache.getCachedVariables(var0).clear();
        ProjectFileCache.getCachedKeywords(var0).clear();
    }

    @Nullable
    private static synchronized PsiElement getCachedElement(@NotNull Project var0, @NotNull String var1) {
        PsiElement var2;
        if ((var2 = ProjectFileCache.getCachedElements(var0).get(var1)) != null) {
            if (var0.isDisposed()) {
                ProjectFileCache.getCachedElements(var0).clear();
                return null;
            }

            if (!var2.isValid()) {
                ProjectFileCache.getCachedElements(var0).remove(var1);
                return null;
            }
        }

        return var2;
    }

    @Nullable
    public static PsiElement findElement(@Nullable String elementName, @NotNull Project project, @NotNull PsiElement contextElement) {
        if (elementName == null) {
            return null;
        } else {
            String cacheKey = contextElement.getContainingFile().getVirtualFile().getParent().getPath() + "#" + elementName;
            PsiElement cachedElement = getCachedElement(project, cacheKey);
            if (cachedElement != null) {
                return cachedElement;
            } else {
                PsiFile psiFile = findPsiFile(elementName, project, contextElement);
                cacheElement(project, cacheKey, psiFile);
                return psiFile;
            }
        }
    }

    private static synchronized void cacheElement(@NotNull Project project, @NotNull String cacheKey, @Nullable PsiElement element) {
        if (element != null && !project.isDisposed()) {
            ProjectFileCache.getCachedElements(project).put(cacheKey, element);
        }
    }

    @Nullable
    public static synchronized PsiElement findElementInContext(@Nullable String elementName, @NotNull Project project, @NotNull PsiElement contextElement) {
        if (elementName == null) {
            return null;
        }

        if (!elementName.endsWith(".py")) {
            PsiElement cachedElement = getCachedElement(project, elementName);
            if (cachedElement != null) {
                return cachedElement;
            }

            PsiElement resolvedElement = PythonResolver.resolveElement(elementName, project);
            if (resolvedElement != null) {
                cacheElement(project, elementName, resolvedElement);
                return resolvedElement;
            }
        } else {
            String cacheKey = contextElement.getContainingFile().getOriginalFile().getVirtualFile().getParent().getPath() + "#" + elementName;
            PsiElement cachedElement = getCachedElement(project, cacheKey);
            if (cachedElement != null) {
                return cachedElement;
            }

            PsiFile psiFile = findPsiFile(elementName, project, ProjectScope.getContentScope(project), contextElement);
            if (psiFile != null) {
                cacheElement(project, cacheKey, psiFile);
                return psiFile;
            }

            psiFile = findPsiFile(elementName, project, contextElement);
            if (psiFile != null) {
                cacheElement(project, cacheKey, psiFile);
                return psiFile;
            }
        }

        return null;
    }

    @Nullable
    private static PsiFile findPsiFile(@NotNull String filePath, @NotNull Project project, @NotNull PsiElement element) {
        return findPsiFile(filePath, project, GlobalSearchScope.allScope(project), element);
    }

    @Nullable
    private static PsiFile findPsiFile(@NotNull String filePath,
                                       @NotNull Project project,
                                       @NotNull GlobalSearchScope searchScope,
                                       @NotNull PsiElement element) {
        try {
            File file = new File(filePath);
            PsiFile[] foundFiles = FilenameIndex.getFilesByName(project, file.getName(), searchScope);
            if (!file.isAbsolute()) {
                String parentPath = element.getContainingFile().getVirtualFile().getParent().getPath();
                file = new File(parentPath, filePath).getCanonicalFile();
            }
            String canonicalPath = file.getPath();
            if (SystemUtils.IS_OS_WINDOWS) {
                canonicalPath = canonicalPath.replace("\\", "/");
            }
            for (PsiFile psiFile : foundFiles) {
                if (psiFile.getVirtualFile().getPath().equals(canonicalPath)) {
                    return psiFile;
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    @NotNull
    public static List<PsiFile> findPsiFiles(@NotNull String filePath, @NotNull Project project) {
        List<PsiFile> psiFiles = new ArrayList<>();

        try {
            File file = new File(filePath);
            PsiFile[] foundFiles = FilenameIndex.getFilesByName(project, file.getName(), GlobalSearchScope.allScope(project));
            if (!file.isAbsolute()) {
                for (PsiFile foundFile : foundFiles) {
                    if (foundFile.getVirtualFile().getPath().endsWith(filePath)) {
                        psiFiles.add(foundFile);
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        return psiFiles;
    }

    public static Map<String, PsiFile> getCachedFiles(Project project) {
        Map<String, PsiFile> cachedFiles = ProjectFileCache.getCachedFiles(project);
        if (!cachedFiles.isEmpty()) {
            return cachedFiles;
        }

        Map<String, PsiFile> result = new HashMap<>();
        try {
            Collection<PyFile> pyFiles = PyModuleNameIndex.findByQualifiedName(QualifiedName.fromDottedString("robot.libraries"),
                                                                               project,
                                                                               PySearchUtilBase.excludeSdkTestsScope(project));

            for (PyFile pyFile : pyFiles) {
                PsiFile[] files = pyFile.getContainingDirectory().getFiles();
                for (PsiFile file : files) {
                    String fileName = file.getName();
                    if (!"__init__.py".equals(fileName) && Character.isUpperCase(fileName.charAt(0))) {
                        String key = fileName.replace(".py", "");
                        result.put(key, file);
                        cachedFiles.put(key, file);
                    }
                }
            }
        } catch (Exception e) {
            cachedFiles.clear();
        }

        return result;
    }

    public static synchronized Collection<DefinedVariable> getGlobalVariables(Project project) {
        Collection<DefinedVariable> globalVariables = ProjectFileCache.getGlobalVariables(project);
        if (globalVariables.isEmpty() || globalVariables.size() != ReservedVariable.values().length) {
            try {
                globalVariables.clear();
                ReservedVariable[] reservedVariables = ReservedVariable.values();
                for (ReservedVariable reservedVariable : reservedVariables) {
                    PsiElement element = reservedVariable.getReferencedPsiElement(project);
                    if (element != null) {
                        globalVariables.add(new VariableDto(element, reservedVariable.getVariable(), reservedVariable.getScope()));
                    }
                }
            } catch (Throwable t) {
                globalVariables.clear();
            }
        }
        return globalVariables;
    }
}
