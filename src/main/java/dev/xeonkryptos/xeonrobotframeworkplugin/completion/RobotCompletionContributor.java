package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.ElementPattern;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotKeywordProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotConditionalStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotExceptionHandlingStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotForLoopStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportGlobalSettingExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSettingId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTasksSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCasesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotWhileLoopStructure;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiComment;
import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.instanceOf;
import static com.intellij.patterns.StandardPatterns.or;
import static dev.xeonkryptos.xeonrobotframeworkplugin.patterns.RobotPatterns.atFirstPositionOf;
import static dev.xeonkryptos.xeonrobotframeworkplugin.patterns.RobotPatterns.indented;
import static dev.xeonkryptos.xeonrobotframeworkplugin.patterns.RobotPatterns.previousNonWhitespaceOrCommentSibling;

public class RobotCompletionContributor extends CompletionContributor {

    public RobotCompletionContributor() {
        extend(CompletionType.BASIC, psiElement().without(indented()).inFile(psiElement(RobotFile.class)), new SectionCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement().andNot(or(psiComment(), psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.VARIABLE_BODY), psiElement(RobotTypes.TEMPLATE_ARGUMENT_VALUE)))
                           .inside(true, instanceOf(RobotSettingsSection.class))
                           .inFile(psiElement(RobotFile.class)),
               new SettingsKeywordCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement().andNot(or(psiComment(), psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.VARIABLE_BODY), psiElement(RobotTypes.TEMPLATE_ARGUMENT_VALUE)))
                           .inside(true, or(instanceOf(RobotTestCaseStatement.class), instanceOf(RobotTaskStatement.class)))
                           .with(indented())
                           .inFile(psiElement(RobotFile.class)),
               new LocalSettingsCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement().andNot(psiComment())
                           .inside(true, or(instanceOf(RobotLibraryImportGlobalSetting.class), instanceOf(RobotResourceImportGlobalSetting.class)))
                           .and(psiElement(RobotTypes.LITERAL_CONSTANT).with(atFirstPositionOf(psiElement(RobotImportGlobalSettingExpression.class))))
                           .inFile(psiElement(RobotFile.class)),
               new ImportCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement().andNot(or(psiComment(), psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.VARIABLE_BODY), psiElement(RobotTypes.TEMPLATE_ARGUMENT_VALUE)))
                           .inside(true, instanceOf(RobotTestCaseStatement.class))
                           .with(indented())
                           .inFile(psiElement(RobotFile.class)),
               new GherkinCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement().andNot(or(psiComment(), psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.VARIABLE_BODY), psiElement(RobotTypes.TEMPLATE_ARGUMENT_VALUE)))
                           .inside(true, or(instanceOf(RobotTestCaseStatement.class), instanceOf(RobotUserKeywordStatement.class), instanceOf(RobotTaskStatement.class)))
                           .without(previousNonWhitespaceOrCommentSibling(psiElement(RobotForLoopStructure.class)))
                           .with(indented())
                           .inFile(psiElement(RobotFile.class)),
               new ControlStructureCompletionProvider(RobotKeywordProvider.SYNTAX_MARKER));
        extend(CompletionType.BASIC,
               psiElement().andNot(or(psiComment(), psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.VARIABLE_BODY), psiElement(RobotTypes.TEMPLATE_ARGUMENT_VALUE)))
                           .inside(true, instanceOf(RobotUserKeywordStatement.class))
                           .without(previousNonWhitespaceOrCommentSibling(psiElement(RobotForLoopStructure.class)))
                           .with(indented())
                           .inFile(psiElement(RobotFile.class)),
               new ControlStructureCompletionProvider(RobotTypes.USER_KEYWORD_STATEMENT));
        extend(CompletionType.BASIC,
               psiElement().andNot(or(psiComment(), psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.VARIABLE_BODY), psiElement(RobotTypes.TEMPLATE_ARGUMENT_VALUE)))
                           .inside(true, or(instanceOf(RobotForLoopStructure.class), instanceOf(RobotWhileLoopStructure.class)))
                           .with(indented())
                           .inFile(psiElement(RobotFile.class)),
               new ControlStructureCompletionProvider(RobotTypes.LOOP_CONTROL_STRUCTURE));
        extend(CompletionType.BASIC,
               psiElement().andNot(or(psiComment(), psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.VARIABLE_BODY), psiElement(RobotTypes.TEMPLATE_ARGUMENT_VALUE)))
                           .inside(true, or(instanceOf(RobotExceptionHandlingStructure.class)))
                           .with(indented())
                           .inFile(psiElement(RobotFile.class)),
               new ControlStructureCompletionProvider(RobotTypes.EXCEPTION_HANDLING_STRUCTURE));
        extend(CompletionType.BASIC,
               psiElement().andNot(or(psiComment(), psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.VARIABLE_BODY), psiElement(RobotTypes.TEMPLATE_ARGUMENT_VALUE)))
                           .inside(true, or(instanceOf(RobotConditionalStructure.class)))
                           .with(indented())
                           .inFile(psiElement(RobotFile.class)),
               new ControlStructureCompletionProvider(RobotTypes.CONDITIONAL_STRUCTURE));
        extend(CompletionType.BASIC,
               psiElement().andNot(or(psiComment(), psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.VARIABLE_BODY), psiElement(RobotTypes.TEMPLATE_ARGUMENT_VALUE)))
                           .inside(true, or(instanceOf(RobotTestCaseStatement.class), instanceOf(RobotUserKeywordStatement.class), instanceOf(RobotTaskStatement.class)))
                           .with(indented())
                           .with(previousNonWhitespaceOrCommentSibling(psiElement(RobotForLoopStructure.class)))
                           .inFile(psiElement(RobotFile.class)),
               new ForLoopControlStructureCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement().andNot(or(psiComment(), psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.VARIABLE_BODY), psiElement(RobotTypes.TEMPLATE_ARGUMENT_VALUE)))
                           .inside(true, or(instanceOf(RobotTestCasesSection.class), instanceOf(RobotTasksSection.class), instanceOf(RobotKeywordsSection.class)))
                           .with(indented())
                           .inFile(psiElement(RobotFile.class)), new KeywordCompletionProvider());
        // Provide parameter completions in context of keyword statements
        extend(CompletionType.BASIC,
               psiElement().andOr(psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.TEMPLATE_ARGUMENT_VALUE))
                           .andNot(psiElement().inside(true, or(instanceOf(RobotParameter.class), instanceOf(RobotTemplateParameter.class))))
                           .inFile(psiElement(RobotFile.class)),
               new KeywordParametersCompletionProvider());
        // Provide completions in context of variables or arguments
        extend(CompletionType.BASIC,
               psiElement().andOr(psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.VARIABLE_BODY), psiElement(RobotTypes.TEMPLATE_ARGUMENT_VALUE), psiElement(RobotTypes.KEYWORD_NAME))
                           .inFile(psiElement(RobotFile.class)),
               new VariableCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement(RobotTypes.LITERAL_CONSTANT).inside(true, withElementInLocalSetting("[Tags]")).inFile(psiElement(RobotFile.class)),
               new StandardTagCompletionProvider());
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        // debugging point
        super.fillCompletionVariants(parameters, result.withPrefixMatcher(new RobotPrefixMatcher(result.getPrefixMatcher().getPrefix())));
    }

    @SuppressWarnings("SameParameterValue")
    private ElementPattern<RobotLocalSetting> withElementInLocalSetting(@NotNull String localSettingId) {
        return psiElement(RobotLocalSetting.class).withFirstChild(psiElement(RobotLocalSettingId.class).withText(localSettingId));
    }
}
