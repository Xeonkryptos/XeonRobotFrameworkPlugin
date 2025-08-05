package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ParameterDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSettingArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;

public final class RobotUserKeywordInputArgumentCollector extends RobotVisitor {

    private final Collection<DefinedParameter> inputArguments = new LinkedHashSet<>();

    private PsiElement argumentReference;
    private String argumentName;

    @Override
    public void visitLocalSettingArgument(@NotNull RobotLocalSettingArgument o) {
        RobotVariable variable = o.getVariable();
        argumentReference = o;
        argumentName = variable.getVariableName();
        o.acceptChildren(this);
        argumentName = null;
        argumentReference = null;
    }

    @Override
    public void visitPositionalArgument(@NotNull RobotPositionalArgument o) {
        boolean resetArgumentReference = argumentReference == null;
        if (argumentReference != null) {
            argumentReference = o;
        }
        o.acceptChildren(this);
        if (resetArgumentReference) {
            argumentReference = null;
        }
    }

    @Override
    public void visitVariable(@NotNull RobotVariable o) {
        String variableName = o.getVariableName();
        if (variableName != null) {
            ParameterDto parameterDto = argumentName != null ?
                                        new ParameterDto(argumentReference, argumentName, o.getText()) :
                                        new ParameterDto(argumentReference, variableName, null);
            inputArguments.add(parameterDto);
        }
    }

    public Collection<DefinedParameter> getInputArguments() {
        return inputArguments;
    }
}
