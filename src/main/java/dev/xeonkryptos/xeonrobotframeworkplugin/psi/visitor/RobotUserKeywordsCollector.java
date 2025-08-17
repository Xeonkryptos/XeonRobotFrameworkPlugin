package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.KeywordDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ParameterDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public final class RobotUserKeywordsCollector extends RobotVisitor {

    private final Set<DefinedKeyword> keywords = new LinkedHashSet<>();

    private final Collection<DefinedParameter> keywordArguments = new LinkedHashSet<>();

    @Override
    public void visitRoot(@NotNull RobotRoot o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitKeywordsSection(@NotNull RobotKeywordsSection o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitUserKeywordStatement(@NotNull RobotUserKeywordStatement o) {
        keywordArguments.clear();

        o.acceptChildren(this);

        String keywordName = o.getName();
        KeywordDto keywordDto = new KeywordDto(o, null, keywordName, keywordArguments);
        keywords.add(keywordDto);
    }

    @Override
    public void visitLocalArgumentsSetting(@NotNull RobotLocalArgumentsSetting o) {
        super.visitLocalArgumentsSetting(o);
        o.acceptChildren(this);
    }

    @Override
    public void visitLocalArgumentsSettingArgument(@NotNull RobotLocalArgumentsSettingArgument o) {
        super.visitLocalArgumentsSettingArgument(o);

        RobotVariableDefinition variableDefinition = o.getVariableDefinition();
        String parameterName = variableDefinition.getName();
        if (parameterName != null) {
            RobotPositionalArgument parameterValueElement = o.getPositionalArgument();

            String defaultValue = parameterValueElement.getText();

            ParameterDto parameterDto = new ParameterDto(variableDefinition, parameterName, defaultValue);
            keywordArguments.add(parameterDto);
        }
    }

    @Override
    public void visitPositionalArgument(@NotNull RobotPositionalArgument o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitVariable(@NotNull RobotVariable o) {
        String parameterName = o.getVariableName();
        ParameterDto parameterDto = new ParameterDto(o, parameterName, null);
        keywordArguments.add(parameterDto);
    }

    public Collection<DefinedKeyword> getKeywords() {
        return keywords;
    }
}
