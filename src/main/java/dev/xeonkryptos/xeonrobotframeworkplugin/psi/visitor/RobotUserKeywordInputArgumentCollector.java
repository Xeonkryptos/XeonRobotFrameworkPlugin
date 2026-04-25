package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ParameterDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingParameterMandatory;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingParameterOptional;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;

@Getter
public final class RobotUserKeywordInputArgumentCollector extends RecursiveRobotVisitor {

    private final Collection<DefinedParameter> inputArguments = new LinkedHashSet<>();

    @Override
    public void visitLocalArgumentsSettingParameterMandatory(@NotNull RobotLocalArgumentsSettingParameterMandatory o) {
        String argumentName = o.getVariableDefinition().getName();
        if (argumentName != null) {
            ParameterDto parameterDto = ParameterDto.builder(o, argumentName).build();
            inputArguments.add(parameterDto);
        }
    }

    @Override
    public void visitLocalArgumentsSettingParameterOptional(@NotNull RobotLocalArgumentsSettingParameterOptional o) {
        RobotVariableDefinition variableDefinition = o.getVariableDefinition();

        String argumentName = variableDefinition.getName();
        if (argumentName != null) {
            String defaultValue = o.getPositionalArgument().getText();

            ParameterDto parameterDto = ParameterDto.builder(variableDefinition, argumentName).defaultValue(defaultValue).build();
            inputArguments.add(parameterDto);
        }
    }
}
