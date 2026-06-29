package dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootModificationTracker;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.CachedValue;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.QualifiedName;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.PyImportStatementBase;
import com.jetbrains.python.psi.search.PySearchUtilBase;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import com.jetbrains.python.psi.stubs.PyModuleNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class PythonResolver {

    private static final Key<CachedValue<PyClass>> BUILT_IN_LIBRARY_CACHE_KEY = Key.create("BUILT_IN_LIBRARY_CACHE");

    public static PyClass getBuiltInClass(PsiFile psiFile) {
        Module module = ModuleUtil.findModuleForPsiElement(psiFile);
        if (module != null) {
            Project project = psiFile.getProject();
            return CachedValuesManager.getManager(project).getCachedValue(module, BUILT_IN_LIBRARY_CACHE_KEY, () -> {
                ProjectRootModificationTracker projectRootModificationTracker = ProjectRootModificationTracker.getInstance(project);
                PyClass builtIn = PythonResolver.findClass(RobotNames.BUILTIN_FULL_PYTHON_NAMESPACE, project, module);
                if (builtIn != null) {
                    return Result.createSingleDependency(builtIn, projectRootModificationTracker);
                }
                return Result.createSingleDependency(null, projectRootModificationTracker);
            }, false);
        }
        return null;
    }

    @Nullable
    public static PsiElement resolveElement(@NotNull String elementName, @NotNull Project project, @Nullable Module module) {
        Map<String, PsiFile> cachedFiles = RobotFileManager.getCachedRobotSystemFiles(project);
        if (elementName.startsWith("robot.libraries.")) {
            elementName = elementName.replace("robot.libraries.", "");
        }
        if (cachedFiles.containsKey(elementName)) {
            return cachedFiles.get(elementName);
        }

        PyClass pyClass = findClass(elementName, project, module);
        if (pyClass != null) {
            return pyClass;
        }

        PyFile pyFile = findModule(elementName, project, module);
        if (pyFile != null) {
            for (PyImportStatementBase importStatement : pyFile.getImportBlock()) {
                for (String fullyQualifiedName : importStatement.getFullyQualifiedObjectNames()) {
                    if (elementName.equals(fullyQualifiedName)) {
                        PyClass importedClass = findClass(elementName + "." + elementName, project, module);
                        if (importedClass != null) {
                            return importedClass;
                        }
                    }
                }
            }
            return pyFile;
        }

        return findClassWithShortName(elementName, project, module);
    }

    @Nullable
    public static PyFile findModule(@NotNull String moduleName, @NotNull Project project, @Nullable Module module) {
        GlobalSearchScope scope = module != null ? createPyElementGlobalSearchScope(project, module) : PySearchUtilBase.excludeSdkTestsScope(project);
        List<PyFile> modules = PyModuleNameIndex.findByQualifiedName(QualifiedName.fromDottedString(moduleName), project, scope);
        if (!modules.isEmpty()) {
            return modules.getFirst();
        }
        return null;
    }

    @Nullable
    public static PyClass findClass(@NotNull String name, @NotNull Project project, @Nullable Module module) {
        String shortName = extractShortName(name);

        Ref<PyClass> matchingClassRef = Ref.create();
        GlobalSearchScope scope = module != null ? createPyElementGlobalSearchScope(project, module) : PySearchUtilBase.excludeSdkTestsScope(project);
        StubIndex.getInstance().processElements(PyClassNameIndex.KEY, shortName, project, scope, PyClass.class, pyClass -> {
            String qName = pyClass.getQualifiedName();
            boolean relevantPyClass = qName != null && (qName.equals(name) || !SystemInfo.isFileSystemCaseSensitive && qName.equalsIgnoreCase(name) || qName.equals(name + "." + shortName)
                                                        || !SystemInfo.isFileSystemCaseSensitive && qName.equalsIgnoreCase(name + "." + shortName));
            if (relevantPyClass) {
                matchingClassRef.set(pyClass);
                return false;
            }
            return true;
        });
        return matchingClassRef.get();
    }

    @Nullable
    public static PyClass findClassWithShortName(@NotNull String elementName, @NotNull Project project, @Nullable Module module) {
        String shortName = extractShortName(elementName);

        Ref<PyClass> matchingClassRef = Ref.create();
        GlobalSearchScope scope = module != null ? createPyElementGlobalSearchScope(project, module) : PySearchUtilBase.excludeSdkTestsScope(project);
        StubIndex.getInstance().processElements(PyClassNameIndex.KEY, shortName, project, scope, PyClass.class, pyClass -> {
            String className = pyClass.getName();
            if (className != null && className.equals(elementName)) {
                matchingClassRef.set(pyClass);
                return false;
            } else {
                String qualifiedName = pyClass.getQualifiedName();
                if (qualifiedName != null && elementName.contains(".")) {
                    QualifiedName elementQualifiedName = QualifiedName.fromDottedString(elementName);
                    QualifiedName classQualifiedName = QualifiedName.fromDottedString(qualifiedName);

                    if (Objects.equals(elementQualifiedName.getFirstComponent(), classQualifiedName.getFirstComponent()) && Objects.equals(elementQualifiedName.getLastComponent(),
                                                                                                                                           classQualifiedName.getLastComponent())) {
                        matchingClassRef.set(pyClass);
                        return false;
                    }
                }
            }
            return true;
        });
        return matchingClassRef.get();
    }

    private static GlobalSearchScope createPyElementGlobalSearchScope(Project project, Module module) {
        return GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, false).uniteWith(GlobalSearchScope.projectScope(project));
    }

    @NotNull
    private static String extractShortName(@NotNull String name) {
        int pos = name.lastIndexOf(".");
        return pos > 0 ? name.substring(pos + 1) : name;
    }
}
