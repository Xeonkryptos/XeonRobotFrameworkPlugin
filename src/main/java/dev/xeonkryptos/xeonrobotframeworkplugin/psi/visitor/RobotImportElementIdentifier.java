package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLiteralConstantValue;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

public final class RobotImportElementIdentifier extends RobotVisitor {

    private boolean importElement;
    private RobotPositionalArgument positionalArgument;

    @Override
    public void visitVariablesImportGlobalSetting(@NotNull RobotVariablesImportGlobalSetting o) {
        importElement = true;
    }

    @Override
    public void visitLibraryImportGlobalSetting(@NotNull RobotLibraryImportGlobalSetting o) {
        importElement = true;
    }

    @Override
    public void visitResourceImportGlobalSetting(@NotNull RobotResourceImportGlobalSetting o) {
        importElement = true;
    }

    @Override
    public void visitPositionalArgument(@NotNull RobotPositionalArgument o) {
        positionalArgument = o;
        o.getParent().accept(this);
    }

    @Override
    public void visitLiteralConstantValue(@NotNull RobotLiteralConstantValue o) {
        o.getParent().accept(this);
    }

    public boolean isImportElement() {
        return importElement && positionalArgument != null;
    }

    public RobotPositionalArgument getPositionalArgument() {
        return positionalArgument;
    }
}
