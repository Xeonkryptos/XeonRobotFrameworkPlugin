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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public final class RobotImportFilesCollector extends RobotVisitor {

    private final Set<KeywordFile> files = new LinkedHashSet<>();

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
        PsiElement resolvedElement = positionalArgument.getReference().resolve();
        if (resolvedElement instanceof KeywordFile keywordFile) {
            files.add(keywordFile);
        }
    }

    @Override
    public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
        RobotPositionalArgument positionalArgument = o.getImportedFile();
        PsiElement resolved = positionalArgument.getReference().resolve();
        RobotNewLibraryName newLibraryNameElement = o.getNewLibraryName();
        String newLibraryName = null;
        if (newLibraryNameElement != null) {
            newLibraryName = newLibraryNameElement.getText();
        }

        if (resolved instanceof PyClass pyClass) {
            files.add(new RobotPythonClass(newLibraryName, pyClass, ImportType.LIBRARY));
        } else if (resolved instanceof PyFile file) {
            files.add(new RobotPythonFile(newLibraryName, file, ImportType.LIBRARY));
        }
    }

    @Override
    public void visitVariablesImportGlobalSetting(@NotNull RobotVariablesImportGlobalSetting o) {
        newLibraryName = null;

        RobotPositionalArgument positionalArgument = o.getImportedFile();
        positionalArgument.acceptChildren(this);

        PsiElement resolved = positionalArgument.getReference().resolve();

        if (resolved instanceof PyClass pyClass) {
            files.add(new RobotPythonClass(newLibraryName, pyClass, ImportType.VARIABLES));
        } else if (resolved instanceof PyFile file) {
            files.add(new RobotPythonFile(newLibraryName, file, ImportType.VARIABLES));
        }
    }

    @Override
    public void visitLiteralConstantValue(@NotNull RobotLiteralConstantValue o) {
        newLibraryName = o.getText();
    }

    public Collection<KeywordFile> getFiles() {
        return files;
    }
}
