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

    private String namespace;

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
        positionalArgument.acceptChildren(this);

        PsiElement resolved = positionalArgument.getReference().resolve();
        RobotNewLibraryName newLibraryName = o.getNewLibraryName();
        boolean isDifferentNamespace = newLibraryName != null;
        if (isDifferentNamespace) {
            namespace = newLibraryName.getText();
        }

        if (resolved instanceof PyClass pyClass) {
            files.add(new RobotPythonClass(namespace, pyClass, ImportType.LIBRARY, isDifferentNamespace));
        } else if (resolved instanceof PyFile file) {
            files.add(new RobotPythonFile(namespace, file, ImportType.LIBRARY, isDifferentNamespace));
        }
    }

    @Override
    public void visitVariablesImportGlobalSetting(@NotNull RobotVariablesImportGlobalSetting o) {
        RobotPositionalArgument positionalArgument = o.getImportedFile();
        positionalArgument.acceptChildren(this);

        PsiElement resolved = positionalArgument.getReference().resolve();

        if (resolved instanceof PyClass pyClass) {
            files.add(new RobotPythonClass(namespace, pyClass, ImportType.LIBRARY, false));
        } else if (resolved instanceof PyFile file) {
            files.add(new RobotPythonFile(namespace, file, ImportType.LIBRARY, false));
        }
    }

    @Override
    public void visitLiteralConstantValue(@NotNull RobotLiteralConstantValue o) {
        namespace = o.getText();
    }

    public Collection<KeywordFile> getFiles() {
        return files;
    }
}
