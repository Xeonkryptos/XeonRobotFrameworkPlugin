package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.VariableScope;

public interface RobotVariableExpression {

    // TODO: Read a configured scope from the variable itself when defined as a parameter or provide the default scope from context
    VariableScope getScope();
}
