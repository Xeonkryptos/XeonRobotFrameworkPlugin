package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLiteralConstantValue;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotNewLibraryName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotPythonClass;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotPythonFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.DisposableSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class RobotImportFilesCollector extends RobotVisitor {

    private final Map<ImportType, Set<DisposableSupplier<KeywordFile>>> keywordFileSuppliers = new EnumMap<>(ImportType.class);

    private String newLibraryName;

    @Override
    public void visitRoot(@NotNull RobotRoot o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitSettingsSection(@NotNull RobotSettingsSection o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitResourceImportGlobalSetting(@NotNull RobotResourceImportGlobalSetting o) {
        RobotImportArgument importArgument = o.getImportedFile();
        if (importArgument != null) {
            RobotBasedKeywordFileSupplier keywordFileSupplier = new RobotBasedKeywordFileSupplier(importArgument);
            keywordFileSuppliers.computeIfAbsent(ImportType.RESOURCE, key -> new LinkedHashSet<>()).add(keywordFileSupplier);
        }
    }

    @Override
    public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
        RobotImportArgument importArgument = o.getImportedFile();
        if (importArgument != null) {
            RobotNewLibraryName newLibraryNameElement = o.getNewLibraryName();
            String newLibraryName = null;
            if (newLibraryNameElement != null) {
                newLibraryName = newLibraryNameElement.getText();
            }

            PythonBasedKeywordFileSupplier keywordFileSupplier = new PythonBasedKeywordFileSupplier(importArgument, ImportType.LIBRARY, newLibraryName);
            keywordFileSuppliers.computeIfAbsent(ImportType.LIBRARY, key -> new LinkedHashSet<>()).add(keywordFileSupplier);
        }
    }

    @Override
    public void visitVariablesImportGlobalSetting(@NotNull RobotVariablesImportGlobalSetting o) {
        newLibraryName = null;

        RobotImportArgument importArgument = o.getImportedFile();
        if (importArgument != null) {
            importArgument.acceptChildren(this);

            DisposableSupplier<KeywordFile> keywordFileSupplier = new PythonBasedKeywordFileSupplier(importArgument, ImportType.VARIABLES, newLibraryName);
            keywordFileSuppliers.computeIfAbsent(ImportType.VARIABLES, key -> new LinkedHashSet<>()).add(keywordFileSupplier);
        }
    }

    @Override
    public void visitLiteralConstantValue(@NotNull RobotLiteralConstantValue o) {
        newLibraryName = o.getText();
    }

    public Map<ImportType, Set<DisposableSupplier<KeywordFile>>> getKeywordFileSuppliers() {
        return keywordFileSuppliers;
    }

    private static class RobotBasedKeywordFileSupplier implements DisposableSupplier<KeywordFile> {

        private final Project project;
        private final SmartPsiElementPointer<RobotImportArgument> importArgumentPointer;

        private RobotBasedKeywordFileSupplier(RobotImportArgument importArgument) {
            project = importArgument.getProject();
            importArgumentPointer = SmartPointerManager.createPointer(importArgument);
        }

        @Override
        public KeywordFile get() {
            RobotImportArgument importArgument = importArgumentPointer.getElement();
            if (importArgument == null) {
                return null;
            }
            PsiElement resolved = importArgument.getReference().resolve();
            if (resolved instanceof KeywordFile keywordFile) {
                return keywordFile;
            }
            return null;
        }

        @Override
        public void dispose() {
            SmartPointerManager.getInstance(project).removePointer(importArgumentPointer);
        }
    }

    private static class PythonBasedKeywordFileSupplier implements DisposableSupplier<KeywordFile> {

        private final Project project;
        private final SmartPsiElementPointer<RobotImportArgument> importArgumentPointer;
        private final ImportType importType;
        private final String libraryName;

        private PythonBasedKeywordFileSupplier(RobotImportArgument importArgument, ImportType importType, String libraryName) {
            this.project = importArgument.getProject();
            this.importArgumentPointer = SmartPointerManager.createPointer(importArgument);

            this.importType = importType;
            this.libraryName = libraryName;
        }

        @Override
        public KeywordFile get() {
            RobotImportArgument importArgument = importArgumentPointer.getElement();
            if (importArgument == null) {
                return null;
            }
            PsiElement resolved = importArgument.getReference().resolve();
            if (resolved instanceof PyClass pyClass) {
                return new RobotPythonClass(libraryName, pyClass, importType);
            } else if (resolved instanceof PyFile file) {
                return new RobotPythonFile(libraryName, file, importType);
            }
            return null;
        }

        @Override
        public void dispose() {
            SmartPointerManager.getInstance(project).removePointer(importArgumentPointer);
        }
    }
}
