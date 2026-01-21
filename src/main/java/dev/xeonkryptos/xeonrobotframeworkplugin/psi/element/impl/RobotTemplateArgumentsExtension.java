package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateArguments;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotCallArgumentsCollector;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RobotTemplateArgumentsExtension extends RobotPsiElementBase implements RobotTemplateArguments {

    private Collection<RobotArgument> allTemplateArguments;

    public RobotTemplateArgumentsExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        allTemplateArguments = null;
    }

    @Override
    public Collection<RobotArgument> getAllCallArguments() {
        if (allTemplateArguments == null) {
            RobotCallArgumentsCollector collector = new RobotCallArgumentsCollector();
            acceptChildren(collector);
            allTemplateArguments = collector.getArguments();
        }
        return allTemplateArguments;
    }

    @Override
    public Collection<RobotArgument> getPositionalArguments() {
        return getTemplateArgumentList().stream().map(argument -> (RobotArgument) argument).collect(Collectors.toList());
    }

    @Override
    public Collection<DefinedParameter> computeMissingParameters() {
        RobotKeywordCall templateKeywordCall = KeywordUtil.findTemplateKeywordCall(this);
        if (templateKeywordCall == null) {
            return List.of();
        }
        return KeywordParameterEvaluator.computeMissingParameters(templateKeywordCall, this);
    }

    @Override
    public Collection<String> computeMissingRequiredParameters() {
        RobotKeywordCall templateKeywordCall = KeywordUtil.findTemplateKeywordCall(this);
        if (templateKeywordCall == null) {
            return List.of();
        }
        return KeywordParameterEvaluator.computeMissingRequiredParameters(templateKeywordCall, this);
    }

    @Override
    public Collection<String> getDefinedParameterNames() {
        return getTemplateParameterList().stream().map(RobotTemplateParameter::getParameterName).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
