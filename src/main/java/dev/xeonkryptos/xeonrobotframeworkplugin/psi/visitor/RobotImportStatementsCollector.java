package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RobotImportStatementsCollector extends RobotVisitor {

    private final List<String> importedFiles = new ArrayList<>();
    private final List<PsiElement> importElements = new ArrayList<>();

    @Override
    public void visitResourceImportGlobalSetting(@NotNull RobotResourceImportGlobalSetting o) {
        RobotPositionalArgument positionalArgument = o.getImportedFile();
        addImportedFile(positionalArgument);
        importElements.add(o);
    }

    @Override
    public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
        RobotPositionalArgument positionalArgument = o.getImportedFile();
        addImportedFile(positionalArgument);
        importElements.add(o);
    }

    @Override
    public void visitVariablesImportGlobalSetting(@NotNull RobotVariablesImportGlobalSetting o) {
        RobotPositionalArgument positionalArgument = o.getImportedFile();
        addImportedFile(positionalArgument);
        importElements.add(o);
    }

    private void addImportedFile(RobotPositionalArgument positionalArgument) {
        String importedFile = positionalArgument.getText();
        importedFiles.add(importedFile);
    }

    public List<String> getImportedFiles() {
        return importedFiles;
    }

    public List<PsiElement> getImportElements() {
        return importElements;
    }
}
