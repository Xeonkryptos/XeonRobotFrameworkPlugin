package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.KeywordDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ParameterDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLiteralConstantValue;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingParameterMandatory;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSettingParameterOptional;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotRoot;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotTag;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public final class RobotUserKeywordsCollector extends RobotVisitor {

    private final Set<DefinedKeyword> keywords = new LinkedHashSet<>();

    private final Collection<DefinedParameter> keywordArguments = new LinkedHashSet<>();

    private boolean lookingForPrivateMarker;

    private boolean markedAsPrivate;

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
        KeywordDto keywordDto = new KeywordDto(o, null, keywordName, keywordArguments, markedAsPrivate);
        keywords.add(keywordDto);
    }

    @Override
    public void visitLocalSetting(@NotNull RobotLocalSetting o) {
        super.visitLocalSetting(o);

        if ("[Tags]".equalsIgnoreCase(o.getSettingName())) {
            lookingForPrivateMarker = true;
            o.acceptChildren(this);
            lookingForPrivateMarker = false;
        }
    }

    @Override
    public void visitLocalArgumentsSetting(@NotNull RobotLocalArgumentsSetting o) {
        super.visitLocalArgumentsSetting(o);
        o.acceptChildren(this);
    }

    @Override
    public void visitLocalArgumentsSettingParameterOptional(@NotNull RobotLocalArgumentsSettingParameterOptional o) {
        super.visitLocalArgumentsSettingParameterOptional(o);

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
    public void visitLocalArgumentsSettingParameterMandatory(@NotNull RobotLocalArgumentsSettingParameterMandatory o) {
        super.visitLocalArgumentsSettingParameterMandatory(o);

        String parameterName = o.getVariableDefinition().getName();
        ParameterDto parameterDto = new ParameterDto(o, parameterName, null);
        keywordArguments.add(parameterDto);
    }

    @Override
    public void visitPositionalArgument(@NotNull RobotPositionalArgument o) {
        o.acceptChildren(this);
    }

    @Override
    public void visitLiteralConstantValue(@NotNull RobotLiteralConstantValue o) {
        super.visitLiteralConstantValue(o);
        if (lookingForPrivateMarker) {
            String privateTagName = RobotTag.PRIVATE.getTag();
            if (privateTagName.equalsIgnoreCase(o.getText())) {
                markedAsPrivate = true;
            }
        }
    }

    public Collection<DefinedKeyword> getKeywords() {
        return keywords;
    }
}
