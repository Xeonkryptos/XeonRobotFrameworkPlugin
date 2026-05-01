package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.json.psi.JsonFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportSettings;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLiteralConstantValue;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotNewLibraryName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.external.file.RobotJsonFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.external.file.RobotPythonClass;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.external.file.RobotPythonFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.external.file.RobotYamlFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.DisposableSupplier;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLFile;

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class RobotImportFilesCollector extends RobotVisitor {

    @Getter
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
    public void visitImportSettings(@NotNull RobotImportSettings o) {
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
            DisposableSupplier<KeywordFile> variablesFileSupplier = new VariableFilesBasedKeywordFileSupplier(importArgument);

            Set<DisposableSupplier<KeywordFile>> disposableSuppliers = keywordFileSuppliers.computeIfAbsent(ImportType.VARIABLES, key -> new LinkedHashSet<>());
            disposableSuppliers.add(keywordFileSupplier);
            disposableSuppliers.add(variablesFileSupplier);
        }
    }

    @Override
    public void visitLiteralConstantValue(@NotNull RobotLiteralConstantValue o) {
        newLibraryName = o.getText();
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
            Ref<KeywordFile> ref = Ref.create();
            PsiElement resolved = importArgument.getReference().resolve();
            PyElementVisitor visitor = new PyElementVisitor() {
                @Override
                public void visitPyClass(@NotNull PyClass node) {
                    ref.set(new RobotPythonClass(libraryName, node, importType));
                }

                @Override
                public void visitPyFile(@NotNull PyFile node) {
                    ref.set(new RobotPythonFile(libraryName, node, importType));
                }
            };
            if (resolved != null) {
                resolved.accept(visitor);
            }
            return ref.get();
        }

        @Override
        public void dispose() {
            SmartPointerManager.getInstance(project).removePointer(importArgumentPointer);
        }
    }

    private static class VariableFilesBasedKeywordFileSupplier implements DisposableSupplier<KeywordFile> {

        private final Project project;
        private final SmartPsiElementPointer<RobotImportArgument> importArgumentPointer;

        private VariableFilesBasedKeywordFileSupplier(RobotImportArgument importArgument) {
            this.project = importArgument.getProject();
            this.importArgumentPointer = SmartPointerManager.createPointer(importArgument);
        }

        @Override
        public KeywordFile get() {
            RobotImportArgument importArgument = importArgumentPointer.getElement();
            if (importArgument == null) {
                return null;
            }
            PsiElement resolved = importArgument.getReference().resolve();
            if (resolved instanceof JsonFile jsonFile) {
                return new RobotJsonFile(jsonFile);
            } else if (resolved instanceof YAMLFile yamlFile) {
                return new RobotYamlFile(yamlFile);
            }
            return null;
        }

        @Override
        public void dispose() {
            SmartPointerManager.getInstance(project).removePointer(importArgumentPointer);
        }
    }
}
