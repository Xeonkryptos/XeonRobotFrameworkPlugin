package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RobotImportStatementsCollector extends RobotVisitor {

    private final List<String> importedFiles = new ArrayList<>();
    private final List<PsiElement> importElements = new ArrayList<>();

    @Override
    public void visitRoot(@NotNull RobotRoot o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitSection(@NotNull RobotSection o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitResourceImportGlobalSetting(@NotNull RobotResourceImportGlobalSetting o) {
        RobotImportArgument importArgument = o.getImportedFile();
        if (importArgument != null) {
            addImportedFile(importArgument);
            importElements.add(o);
        }
    }

    @Override
    public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
        RobotImportArgument importArgument = o.getImportedFile();
        if (importArgument != null) {
            addImportedFile(importArgument);
            importElements.add(o);
        }
    }

    @Override
    public void visitVariablesImportGlobalSetting(@NotNull RobotVariablesImportGlobalSetting o) {
        RobotImportArgument importArgument = o.getImportedFile();
        if (importArgument != null) {
            addImportedFile(importArgument);
            importElements.add(o);
        }
    }

    private void addImportedFile(RobotImportArgument importArgument) {
        String importedFile = importArgument.getText();
        importedFiles.add(importedFile);
    }

    public List<String> getImportedFiles() {
        return importedFiles;
    }

    public List<PsiElement> getImportElements() {
        return importElements;
    }
}
