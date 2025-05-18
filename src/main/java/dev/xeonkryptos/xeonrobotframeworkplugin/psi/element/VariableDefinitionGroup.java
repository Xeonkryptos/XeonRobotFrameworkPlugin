package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import java.util.Collection;

public interface VariableDefinitionGroup extends RobotStatement {

    default boolean hasMoreThanOneVariable() {
        return getDefinedVariables().size() > 1;
    }

    Collection<DefinedVariable> getDefinedVariables();
}
