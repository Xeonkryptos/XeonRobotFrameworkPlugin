package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.VariableDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedVariable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.util.ReservedVariable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.ProjectScope;
import com.intellij.psi.util.QualifiedName;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.search.PySearchUtilBase;
import com.jetbrains.python.psi.stubs.PyModuleNameIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RobotFileManager {

    private RobotFileManager() {
    }

    @Nullable
    public static PsiElement findElement(@Nullable String elementName, @NotNull Project project, @NotNull PsiElement contextElement) {
        if (elementName == null) {
            return null;
        }
        return findPsiFile(elementName, project, contextElement);
    }

    @Nullable
    public static synchronized PsiElement findElementInContext(@Nullable String elementName, @NotNull Project project, @NotNull PsiElement contextElement) {
        if (elementName == null) {
            return null;
        }

        if (!elementName.endsWith(".py")) {
            return PythonResolver.resolveElement(elementName, project);
        } else {
            PsiFile psiFile = findPsiFile(elementName, project, ProjectScope.getContentScope(project), contextElement);
            if (psiFile != null) {
                return psiFile;
            }
            psiFile = findPsiFile(elementName, project, contextElement);
            return psiFile;
        }
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
        Path file = Path.of(filePath);
        Collection<VirtualFile> foundFiles = FilenameIndex.getVirtualFilesByName(file.getFileName().toString(), searchScope);
        if (!file.isAbsolute()) {
            Optional<String> parentFilePathOpt = Optional.ofNullable(element.getContainingFile().getVirtualFile())
                                                         .map(VirtualFile::getParent)
                                                         .map(VirtualFile::getParent)
                                                         .map(VirtualFile::getPath);
            if (parentFilePathOpt.isEmpty()) {
                return null;
            }
            String parentPath = parentFilePathOpt.get();
            file = Path.of(parentPath, filePath);
        }
        String canonicalPath = file.toAbsolutePath().toString();
        if (SystemInfo.isWindows) {
            canonicalPath = canonicalPath.replace("\\", "/");
        }
        PsiManager psiManager = PsiManager.getInstance(project);
        for (VirtualFile virtualFile : foundFiles) {
            String virtualFilePath = virtualFile.getPath();
            boolean samePath = !SystemInfo.isFileSystemCaseSensitive ? virtualFilePath.equalsIgnoreCase(canonicalPath) : virtualFilePath.equals(canonicalPath);
            if (samePath) {
                return psiManager.findFile(virtualFile);
            }
        }
        return null;
    }

    @NotNull
    public static List<PsiFile> findPsiFiles(@NotNull String filePath, @NotNull Project project) {
        List<PsiFile> psiFiles = new ArrayList<>();
        File file = new File(filePath);
        Collection<VirtualFile> foundFiles = FilenameIndex.getVirtualFilesByName(file.getName(), GlobalSearchScope.allScope(project));
        if (!file.isAbsolute()) {
            PsiManager psiManager = PsiManager.getInstance(project);
            for (VirtualFile foundFile : foundFiles) {
                if (foundFile.getPath().endsWith(filePath)) {
                    PsiFile psiFile = psiManager.findFile(foundFile);
                    psiFiles.add(psiFile);
                }
            }
        }
        return psiFiles;
    }

    public static synchronized Map<String, PsiFile> getCachedRobotSystemFiles(Project project) {
        Map<String, PsiFile> cachedFiles = ProjectFileCache.getCachedRobotSystemFiles(project);
        if (!cachedFiles.isEmpty()) {
            return cachedFiles;
        }

        Map<String, PsiFile> result = new HashMap<>();
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

        return result;
    }

    public static synchronized Collection<DefinedVariable> getGlobalVariables(Project project) {
        Collection<DefinedVariable> globalVariables = ProjectFileCache.getGlobalVariables(project);
        if (globalVariables.isEmpty() || globalVariables.size() != ReservedVariable.values().length) {
            globalVariables.clear();
            ReservedVariable[] reservedVariables = ReservedVariable.values();
            for (ReservedVariable reservedVariable : reservedVariables) {
                PsiElement element = reservedVariable.getReferencedPsiElement(project);
                if (element != null) {
                    globalVariables.add(new VariableDto(element, reservedVariable.getVariable(), reservedVariable.getScope()));
                }
            }
        }
        return globalVariables;
    }
}
