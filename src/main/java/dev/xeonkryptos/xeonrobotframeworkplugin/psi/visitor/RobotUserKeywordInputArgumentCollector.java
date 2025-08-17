package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ParameterDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;

public final class RobotUserKeywordInputArgumentCollector extends RobotVisitor {

    private final Collection<DefinedParameter> inputArguments = new LinkedHashSet<>();

    @Override
    public void visitLocalArgumentsSettingArgument(@NotNull RobotLocalArgumentsSettingArgument o) {
        super.visitLocalArgumentsSettingArgument(o);
        RobotVariableDefinition variableDefinition = o.getVariableDefinition();

        String argumentName = variableDefinition.getName();
        if (argumentName != null) {
            String defaultValue = o.getPositionalArgument().getText();

            ParameterDto parameterDto = new ParameterDto(variableDefinition, argumentName, defaultValue);
            inputArguments.add(parameterDto);
        }
    }

    @Override
    public void visitVariableDefinition(@NotNull RobotVariableDefinition o) {
        super.visitVariableDefinition(o);

        String argumentName = o.getName();
        if (argumentName != null) {
            ParameterDto parameterDto = new ParameterDto(o, argumentName, null);
            inputArguments.add(parameterDto);
        }
    }

    public Collection<DefinedParameter> getInputArguments() {
        return inputArguments;
    }
}
