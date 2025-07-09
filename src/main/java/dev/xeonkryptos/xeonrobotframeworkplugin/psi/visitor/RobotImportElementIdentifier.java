package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

public final class RobotImportElementIdentifier extends RobotVisitor {

    private boolean importElement;
    private boolean importByVariable;
    private RobotPositionalArgument positionalArgument;

    @Override
    public void visitImportGlobalSetting(@NotNull RobotImportGlobalSetting o) {
        importElement = true;
        o.getImportedFile().accept(this);
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
