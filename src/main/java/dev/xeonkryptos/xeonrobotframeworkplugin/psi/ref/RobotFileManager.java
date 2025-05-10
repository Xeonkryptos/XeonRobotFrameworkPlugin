package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.QualifiedName;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.search.PySearchUtilBase;
import com.jetbrains.python.psi.stubs.PyModuleNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobotFileManager {

    private RobotFileManager() {
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
