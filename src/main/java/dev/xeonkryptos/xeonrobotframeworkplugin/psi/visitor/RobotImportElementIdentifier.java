package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

public final class RobotImportElementIdentifier extends RobotVisitor {

    private boolean importElement;
    private boolean importByVariable;
    private RobotPositionalArgument positionalArgument;

    @Override
    public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
        importElement = true;
        o.getImportedFile().accept(this);
    }

    @Override
    public void visitResourceImportGlobalSetting(@NotNull RobotResourceImportGlobalSetting o) {
        importElement = true;
    }

    @Override
    public void visitVariablesImportGlobalSetting(@NotNull RobotVariablesImportGlobalSetting o) {
        importElement = true;
    }

    @Override
    public void visitPositionalArgument(@NotNull RobotPositionalArgument o) {
        positionalArgument = o;
        o.acceptChildren(this);
    }

    @Override
    public void visitVariable(@NotNull RobotVariable o) {
        importByVariable = true;
    }

    public boolean isImportElement() {
        return importElement;
    }

    public boolean isImportByVariable() {
        return importByVariable;
    }

    public RobotPositionalArgument getPositionalArgument() {
        return positionalArgument;
    }
}
