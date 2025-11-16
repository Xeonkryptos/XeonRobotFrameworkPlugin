package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.QualifiedName;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import com.jetbrains.python.psi.PyImportStatementBase;
import com.jetbrains.python.psi.search.PySearchUtilBase;
import com.jetbrains.python.psi.stubs.PyClassNameIndex;
import com.jetbrains.python.psi.stubs.PyModuleNameIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class PythonResolver {

    @Nullable
    public static PsiElement resolveElement(@NotNull String elementName, @NotNull Project project) {
        Map<String, PsiFile> cachedFiles = RobotFileManager.getCachedRobotSystemFiles(project);
        if (elementName.startsWith("robot.libraries.")) {
            elementName = elementName.replace("robot.libraries.", "");
        }
        if (cachedFiles.containsKey(elementName)) {
            return cachedFiles.get(elementName);
        }

        PyClass pyClass = findClass(elementName, project);
        if (pyClass != null) {
            return pyClass;
        }

        PyFile pyFile = findModule(elementName, project);
        if (pyFile != null) {
            for (PyImportStatementBase importStatement : pyFile.getImportBlock()) {
                for (String fullyQualifiedName : importStatement.getFullyQualifiedObjectNames()) {
                    if (elementName.equals(fullyQualifiedName)) {
                        PyClass importedClass = findClass(elementName + "." + elementName, project);
                        if (importedClass != null) {
                            return importedClass;
                        }
                    }
                }
            }
            return pyFile;
        }

        String shortName = extractShortName(elementName);
        Collection<PyClass> classes = PyClassNameIndex.find(shortName, project, true);
        List<PyClass> matchingClasses = new ArrayList<>();

        for (PyClass cls : classes) {
            String className = cls.getName();
            if (className != null && className.equals(elementName)) {
                matchingClasses.add(cls);
            } else {
                String qualifiedName = cls.getQualifiedName();
                if (qualifiedName != null && elementName.contains(".")) {
                    QualifiedName elementQualifiedName = QualifiedName.fromDottedString(elementName);
                    QualifiedName classQualifiedName = QualifiedName.fromDottedString(qualifiedName);

                    if (Objects.equals(elementQualifiedName.getFirstComponent(), classQualifiedName.getFirstComponent())
                        && Objects.equals(elementQualifiedName.getLastComponent(), classQualifiedName.getLastComponent())) {
                        matchingClasses.add(cls);
                    }
                }
            }
        }
        return matchingClasses.isEmpty() ? null : matchingClasses.getFirst();
    }

    @Nullable
    public static PyFile findModule(@NotNull String moduleName, @NotNull Project project) {
        List<PyFile> modules = PyModuleNameIndex.findByQualifiedName(QualifiedName.fromDottedString(moduleName),
                                                                     project,
                                                                     PySearchUtilBase.excludeSdkTestsScope(project));
        for (PyFile pyFile : modules) {
            if (pyFile.isValid()) {
                return pyFile;
            }
        }
        return null;
    }

    @Nullable
    public static PyClass findClass(@NotNull String name, @NotNull Project project) {
        String shortName = extractShortName(name);
        Collection<PyClass> classes = PyClassNameIndex.find(shortName, project, true);

        List<PyClass> matchedByNames = new ArrayList<>();
        for (PyClass pyClass : classes) {
            String qName = pyClass.getQualifiedName();
            if (qName != null) {
                if (qName.equals(name) || !SystemInfo.isFileSystemCaseSensitive && qName.equalsIgnoreCase(name)) {
                    matchedByNames.add(pyClass);
                } else if (qName.equals(name + "." + shortName) || !SystemInfo.isFileSystemCaseSensitive && qName.equalsIgnoreCase(name + "." + shortName)) {
                    matchedByNames.add(pyClass);
                }
            }
        }

        if (matchedByNames.isEmpty()) {
            return null;
        } else {
            PyClass matchedByName = null;
            for (PyClass pyClass : matchedByNames) {
                if (pyClass.getContainingFile().getName().endsWith(".pyi")) {
                    matchedByName = pyClass;
                }
            }
            return matchedByName != null ? matchedByName : matchedByNames.getFirst();
        }
    }

    @NotNull
    private static String extractShortName(@NotNull String name) {
        int pos = name.lastIndexOf(".");
        return pos > 0 ? name.substring(pos + 1) : name;
    }
}
