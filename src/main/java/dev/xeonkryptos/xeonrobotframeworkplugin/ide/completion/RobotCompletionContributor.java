package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotBlockOpeningStructure;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotImportGlobalSetting;
import org.jetbrains.annotations.NotNull;

import static com.intellij.patterns.PlatformPatterns.psiComment;
import static com.intellij.patterns.PlatformPatterns.psiElement;

public class RobotCompletionContributor extends CompletionContributor {

    public RobotCompletionContributor() {
        extend(CompletionType.BASIC,
               psiElement().andNot(psiComment())
                           .andNot(withTagsWithoutCodeCompletionSupport())
                           .andNot(withArgumentInKeywordStatement())
                           .andNot(psiElement(RobotBlockOpeningStructure.class))
                           .inFile(psiElement(RobotFile.class)),
               new SectionCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement().andNot(psiComment())
                           .andNot(withTagsWithoutCodeCompletionSupport())
                           .andNot(psiElement(RobotTypes.POSITIONAL_ARGUMENT))
                           .inFile(psiElement(RobotFile.class)),
               new SettingsKeywordCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement().andNot(psiComment())
                           .andNot(withTagsWithoutCodeCompletionSupport())
                           .andNot(psiElement(RobotTypes.POSITIONAL_ARGUMENT))
                           .andNot(psiElement(RobotTypes.PARAMETER))
                           .inFile(psiElement(RobotFile.class)),
               new LocalSettingsCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement().andNot(psiComment())
                           .andNot(withTagsWithoutCodeCompletionSupport())
                           .and(psiElement(RobotTypes.POSITIONAL_ARGUMENT).withSuperParent(2, psiElement(RobotImportGlobalSetting.class)))
                           .inFile(psiElement(RobotFile.class)),
               new ImportCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement().andNot(psiComment())
                           .andNot(withTagsWithoutCodeCompletionSupport())
                           .andNot(psiElement(RobotTypes.POSITIONAL_ARGUMENT))
                           .andNot(psiElement(RobotTypes.PARAMETER))
                           .inFile(psiElement(RobotFile.class)),
               new GherkinCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement().andNot(withTagsWithoutCodeCompletionSupport())
                           .andNot(psiComment())
                           .andNot(withArgumentInKeywordStatement())
                           .inFile(psiElement(RobotFile.class)),
               new ControlStructureCompletionProvider());
        extend(CompletionType.BASIC,
               psiElement().andNot(withTagsWithoutCodeCompletionSupport())
                           .andNot(psiComment())
                           .andNot(withArgumentInKeywordStatement())
                           .inFile(psiElement(RobotFile.class)),
               new KeywordCompletionProvider());
        // Provide parameter completions in context of keyword statements
        extend(CompletionType.BASIC,
               psiElement().andNot(psiComment())
                           .andNot(withTagsWithoutCodeCompletionSupport())
                           .and(withArgumentInKeywordStatement())
                           .inFile(psiElement(RobotFile.class)),
               new KeywordParametersCompletionProvider());
        // Provide completions in context of variables or arguments
        extend(CompletionType.BASIC,
               psiElement().andNot(withTagsWithoutCodeCompletionSupport())
                           .andOr(psiElement(RobotTypes.VARIABLE), psiElement(RobotTypes.POSITIONAL_ARGUMENT))
                           .inFile(psiElement(RobotFile.class)),
               new VariableCompletionProvider());
    }

    private static PsiElementPattern.Capture<PsiElement> withArgumentInKeywordStatement() {
        return psiElement().andOr(psiElement(RobotTypes.PARAMETER), psiElement(RobotTypes.POSITIONAL_ARGUMENT))
                           .withAncestor(3, psiElement(RobotTypes.KEYWORD_CALL));
    }

    private static PsiElementPattern.Capture<PsiElement> withTagsWithoutCodeCompletionSupport() {
        return psiElement(RobotTypes.POSITIONAL_ARGUMENT).withAncestor(2,
                                                                       psiElement(RobotTypes.LOCAL_SETTING).withName("[Documentation]",
                                                                                                                     "[Tags]",
                                                                                                                     "[Arguments]"));
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        // debugging point
        super.fillCompletionVariants(parameters, result);
    }
}
