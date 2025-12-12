package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyClass;
import com.jetbrains.python.psi.PyFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLiteralConstantValue;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotNewLibraryName;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotPythonClass;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.RobotPythonFile;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class RobotImportFilesCollector extends RobotVisitor {

    private final Map<ImportType, Set<Supplier<KeywordFile>>> keywordFileSuppliers = new EnumMap<>(ImportType.class);

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
        RobotPositionalArgument positionalArgument = o.getImportedFile();
        if (positionalArgument != null) {
            RobotBasedKeywordFileSupplier keywordFileSupplier = new RobotBasedKeywordFileSupplier(positionalArgument);
            keywordFileSuppliers.computeIfAbsent(ImportType.RESOURCE, key -> new LinkedHashSet<>()).add(keywordFileSupplier);
        }
    }

    @Override
    public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
        RobotPositionalArgument positionalArgument = o.getImportedFile();
        if (positionalArgument != null) {
            RobotNewLibraryName newLibraryNameElement = o.getNewLibraryName();
            String newLibraryName = null;
            if (newLibraryNameElement != null) {
                newLibraryName = newLibraryNameElement.getText();
            }

            PythonBasedKeywordFileSupplier keywordFileSupplier = new PythonBasedKeywordFileSupplier(positionalArgument, ImportType.LIBRARY, newLibraryName);
            keywordFileSuppliers.computeIfAbsent(ImportType.LIBRARY, key -> new LinkedHashSet<>()).add(keywordFileSupplier);
        }
    }

    @Override
    public void visitVariablesImportGlobalSetting(@NotNull RobotVariablesImportGlobalSetting o) {
        newLibraryName = null;

        RobotPositionalArgument positionalArgument = o.getImportedFile();
        if (positionalArgument != null) {
            positionalArgument.acceptChildren(this);

            Supplier<KeywordFile> keywordFileSupplier = new PythonBasedKeywordFileSupplier(positionalArgument, ImportType.VARIABLES, newLibraryName);
            keywordFileSuppliers.computeIfAbsent(ImportType.VARIABLES, key -> new LinkedHashSet<>()).add(keywordFileSupplier);
        }
    }

    @Override
    public void visitLiteralConstantValue(@NotNull RobotLiteralConstantValue o) {
        newLibraryName = o.getText();
    }

    public Map<ImportType, Set<Supplier<KeywordFile>>> getKeywordFileSuppliers() {
        return keywordFileSuppliers;
    }

    private record RobotBasedKeywordFileSupplier(RobotPositionalArgument positionalArgument) implements Supplier<KeywordFile> {

        @Override
        public KeywordFile get() {
            PsiElement resolved = positionalArgument.getReference().resolve();
            if (resolved instanceof KeywordFile keywordFile) {
                return keywordFile;
            }
            return null;
        }
    }

    private record PythonBasedKeywordFileSupplier(RobotPositionalArgument positionalArgument, ImportType importType, String libraryName)
            implements Supplier<KeywordFile> {

        @Override
        public KeywordFile get() {
            PsiElement resolved = positionalArgument.getReference().resolve();
            if (resolved instanceof PyClass pyClass) {
                return new RobotPythonClass(libraryName, pyClass, importType);
            } else if (resolved instanceof PyFile file) {
                return new RobotPythonFile(libraryName, file, importType);
            }
            return null;
        }
    }
}
