package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VfsUtil;
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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
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
    public static PsiElement findElementInContext(@Nullable String elementName, @NotNull Project project, @NotNull PsiElement contextElement) {
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

    @Nullable
    public static PsiFile findPsiFiles(@NotNull String filePath, @Nullable VirtualFile sourceFile, @NotNull Project project) {
        Path file = Path.of(filePath);
        PsiManager psiManager = PsiManager.getInstance(project);
        VirtualFile foundFile = null;
        if (!file.isAbsolute()) {
            if (sourceFile != null) {
                foundFile = sourceFile.findFileByRelativePath("../" + filePath);
            }
        } else {
            foundFile = VfsUtil.findFile(file, true);
        }
        if (foundFile == null) {
            Collection<VirtualFile> foundFiles = FilenameIndex.getVirtualFilesByName(file.getFileName().toString(), GlobalSearchScope.allScope(project));
            for (VirtualFile potentialFile : foundFiles) {
                if (potentialFile.getPath().endsWith(filePath)) {
                    foundFile = potentialFile;
                    break;
                }
            }
        }
        if (foundFile != null) {
            return psiManager.findFile(foundFile);
        }
        return null;
    }

    public static Map<String, PsiFile> getCachedRobotSystemFiles(Project project) {
        Map<String, PsiFile> cachedFiles = ProjectFileCache.getCachedRobotSystemFiles(project);
        synchronized (cachedFiles) {
            if (!cachedFiles.isEmpty()) {
                return cachedFiles;
            }

            Collection<PyFile> pyFiles = PyModuleNameIndex.findByQualifiedName(QualifiedName.fromDottedString("robot.libraries"),
                                                                               project,
                                                                               PySearchUtilBase.excludeSdkTestsScope(project));
            for (PyFile pyFile : pyFiles) {
                PsiFile[] files = pyFile.getContainingDirectory().getFiles();
                for (PsiFile file : files) {
                    String fileName = file.getName();
                    if (!"__init__.py".equals(fileName) && Character.isUpperCase(fileName.charAt(0))) {
                        String key = fileName.replace(".py", "");
                        cachedFiles.put(key, file);
                    }
                }
            }
            return cachedFiles;
        }
    }

    public static Collection<DefinedVariable> getGlobalVariables(Project project) {
        Collection<DefinedVariable> globalVariables = ProjectFileCache.getGlobalVariables(project);
        synchronized (globalVariables) {
            if (globalVariables.isEmpty() || globalVariables.size() != ReservedVariable.values().length) {
                globalVariables.clear();
                ReservedVariable[] reservedVariables = ReservedVariable.values();
                for (ReservedVariable reservedVariable : reservedVariables) {
                    PsiElement element = reservedVariable.getReferencedPsiElement(project);
                    if (element != null) {
                        globalVariables.add(new VariableDto(element,
                                                            reservedVariable.getVariable(),
                                                            reservedVariable.getUnwrappedVariable(),
                                                            reservedVariable.getScope()));
                    }
                }
            }
            return new ArrayList<>(globalVariables);
        }
    }
}
