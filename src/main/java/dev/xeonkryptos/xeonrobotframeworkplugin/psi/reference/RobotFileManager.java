package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.ParameterizedCachedValue;
import com.intellij.psi.util.QualifiedName;
import com.jetbrains.python.PyNames;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.search.PySearchUtilBase;
import com.jetbrains.python.psi.stubs.PyModuleNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.ReservedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class RobotFileManager {

    private static final Key<ParameterizedCachedValue<Collection<DefinedVariable>, Module>> GLOBAL_ROBOT_VARIABLES_CACHE_KEY = Key.create("GLOBAL_ROBOT_VARIABLES_CACHE");

    private RobotFileManager() {
    }

    @Nullable
    public static VirtualFile findContentRootForFile(PsiFile file) {
        VirtualFile sourceFile = file.getVirtualFile();
        if (sourceFile == null) {
            file = file.getOriginalFile();
            sourceFile = file.getVirtualFile();
        }
        Module moduleForFile = ModuleUtilCore.findModuleForFile(file);
        if (moduleForFile != null) {
            VirtualFile[] contentRoots = ModuleRootManager.getInstance(moduleForFile).getContentRoots();
            for (VirtualFile contentRoot : contentRoots) {
                if (VfsUtil.isAncestor(contentRoot, sourceFile, true)) {
                    return contentRoot;
                }
            }
        }
        return null;
    }

    public static Map<String, PsiFile> getCachedRobotSystemFiles(Project project) {
        Map<String, PsiFile> cachedFiles = ProjectFileCache.getCachedRobotSystemFiles(project);
        synchronized (cachedFiles) {
            if (!cachedFiles.isEmpty()) {
                return cachedFiles;
            }

            Collection<PyFile> pyFiles = PyModuleNameIndex.findByQualifiedName(QualifiedName.fromDottedString("robot.libraries"), project, PySearchUtilBase.excludeSdkTestsScope(project));
            for (PyFile pyFile : pyFiles) {
                PsiFile[] files = pyFile.getContainingDirectory().getFiles();
                for (PsiFile file : files) {
                    String fileName = file.getName();
                    if (!PyNames.INIT_DOT_PY.equals(fileName) && Character.isUpperCase(fileName.charAt(0))) {
                        String key = fileName.replace(PyNames.DOT_PY, "");
                        cachedFiles.put(key, file);
                    }
                }
            }
            return cachedFiles;
        }
    }

    public static Collection<DefinedVariable> getGlobalVariables(PsiElement anchor) {
        Project project = anchor.getProject();
        Module module = ModuleUtilCore.findModuleForPsiElement(anchor);
        CachedValuesManager manager = CachedValuesManager.getManager(project);
        UserDataHolder usableUserDataHolder = module != null ? module : project;
        return manager.getParameterizedCachedValue(usableUserDataHolder, GLOBAL_ROBOT_VARIABLES_CACHE_KEY, pModule -> {
            Collection<DefinedVariable> globalVariables = new ArrayList<>();
            ReservedVariable[] reservedVariables = ReservedVariable.values();
            PsiElement element = VariableScope.getReferencedPsiElement(project, module);
            if (element != null) {
                for (ReservedVariable reservedVariable : reservedVariables) {
                    globalVariables.add(new VariableDto(element, reservedVariable.getVariable(), reservedVariable.getVariableType(), reservedVariable.getScope()));
                }
            }
            return element != null ? Result.createSingleDependency(globalVariables, element) : null;
        }, false, module);
    }
}
