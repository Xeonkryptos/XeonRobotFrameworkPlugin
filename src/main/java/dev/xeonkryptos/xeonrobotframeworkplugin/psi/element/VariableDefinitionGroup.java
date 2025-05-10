package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.Collection;

public interface VariableDefinitionGroup extends RobotStatement {

    Collection<DefinedVariable> getDefinedVariables();
}
