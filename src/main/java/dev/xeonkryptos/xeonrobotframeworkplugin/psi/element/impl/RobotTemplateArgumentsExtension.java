package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateArguments;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.KeywordUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotCallArgumentsCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RobotTemplateArgumentsExtension extends RobotPsiElementBase implements RobotTemplateArguments {

    private Collection<RobotArgument> allTemplateArguments;
    private Collection<String> definedParameterNames;

    public RobotTemplateArgumentsExtension(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        allTemplateArguments = null;
        definedParameterNames = null;
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
    public Collection<DefinedParameter> computeMissingParameters() {
        Project project = getProject();
        RobotKeywordCall templateKeywordCall = KeywordUtil.getInstance(project).findTemplateKeywordCall(this);
        if (templateKeywordCall == null) {
            return List.of();
        }
        return KeywordParameterEvaluator.computeMissingParameters(templateKeywordCall, this);
    }

    @Override
    public Collection<String> computeMissingRequiredParameters() {
        Project project = getProject();
        RobotKeywordCall templateKeywordCall = KeywordUtil.getInstance(project).findTemplateKeywordCall(this);
        if (templateKeywordCall == null) {
            return List.of();
        }
        return KeywordParameterEvaluator.computeMissingRequiredParameters(templateKeywordCall, this);
    }

    @Override
    public Collection<String> getDefinedParameterNames() {
        if (definedParameterNames == null) {
            definedParameterNames = getTemplateParameterList().stream()
                                                              .map(RobotTemplateParameter::getParameterName)
                                                              .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        return definedParameterNames;
    }
}
