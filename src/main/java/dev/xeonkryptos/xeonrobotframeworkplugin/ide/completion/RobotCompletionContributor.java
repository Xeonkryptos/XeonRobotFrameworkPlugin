package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.ElementPattern;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportGlobalSettingExpression;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLibraryImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSettingId;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotResourceImportGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesSection;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiComment;
import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.StandardPatterns.instanceOf;
import static com.intellij.patterns.StandardPatterns.or;
import static dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion.RobotPatterns.atFirstPositionOf;

public class RobotCompletionContributor extends CompletionContributor {

    public RobotCompletionContributor() {
        extend(CompletionType.BASIC, psiElement().andNot(psiComment()).inFile(psiElement(RobotFile.class)), new SectionCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement().andNot(or(psiComment(), psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.VARIABLE_BODY)))
                           .inside(true, instanceOf(RobotSettingsSection.class))
                           .inFile(psiElement(RobotFile.class)),
               new SettingsKeywordCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement().andNot(or(psiComment(), psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.VARIABLE_BODY)))
                           .inside(true, or(instanceOf(RobotTestCaseStatement.class), instanceOf(RobotTaskStatement.class)))
                           .inFile(psiElement(RobotFile.class)),
               new LocalSettingsCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement().andNot(psiComment())
                           .inside(true, or(instanceOf(RobotLibraryImportGlobalSetting.class), instanceOf(RobotResourceImportGlobalSetting.class)))
                           .and(psiElement(RobotTypes.LITERAL_CONSTANT).with(atFirstPositionOf(psiElement(RobotImportGlobalSettingExpression.class))))
                           .inFile(psiElement(RobotFile.class)),
               new ImportCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement().andNot(or(psiComment(), psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.VARIABLE_BODY)))
                           .inside(true, instanceOf(RobotTestCaseStatement.class))
                           .inFile(psiElement(RobotFile.class)),
               new GherkinCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement().andNot(or(psiComment(), psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.VARIABLE_BODY)))
                           .inside(true,
                                   or(instanceOf(RobotTestCaseStatement.class),
                                      instanceOf(RobotUserKeywordStatement.class),
                                      instanceOf(RobotTaskStatement.class)))
                           .inFile(psiElement(RobotFile.class)),
               new ControlStructureCompletionProvider());
        extend(CompletionType.BASIC,
               // TODO: There are keywords executing other keywords. Keywords are provided as arguments to those keywords.
               //  Extend code completion to support those keywords and provide code completion for keywords in argument list.
               psiElement().andNot(or(psiComment(), psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.VARIABLE_BODY)))
                           // Exclude keywords suggestions in variables section. It isn't supported to call keywords in variables section.
                           .andNot(psiElement().inside(RobotVariablesSection.class)).inFile(psiElement(RobotFile.class)), new KeywordCompletionProvider());
        // Provide parameter completions in context of keyword statements
        extend(CompletionType.BASIC,
               psiElement(RobotTypes.LITERAL_CONSTANT).andNot(psiElement().inside(true, instanceOf(RobotParameter.class))).inFile(psiElement(RobotFile.class)),
               new KeywordParametersCompletionProvider());
        // Provide completions in context of variables or arguments
        extend(CompletionType.BASIC,
               psiElement().andOr(psiElement(RobotTypes.LITERAL_CONSTANT), psiElement(RobotTypes.VARIABLE_BODY)).inFile(psiElement(RobotFile.class)),
               new VariableCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement(RobotTypes.LITERAL_CONSTANT).inside(true, withElementInLocalSetting("[Tags]")).inFile(psiElement(RobotFile.class)),
               new StandardTagCompletionProvider());
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        // debugging point
        super.fillCompletionVariants(parameters, result);
    }

    @SuppressWarnings("SameParameterValue")
    private ElementPattern<RobotLocalSetting> withElementInLocalSetting(@NotNull String localSettingId) {
        return psiElement(RobotLocalSetting.class).withFirstChild(psiElement(RobotLocalSettingId.class).withText(localSettingId));
    }
}
