package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotStubTokenTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenTypes;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import org.jetbrains.annotations.NotNull;

public class RobotCompletionContributor extends CompletionContributor {

    public RobotCompletionContributor() {
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andNot(PlatformPatterns.psiComment())
                               .andNot(withTagsWithoutCodeCompletionSupport())
                               .andNot(withArgumentInKeywordStatement())
                               .andNot(PlatformPatterns.psiElement(RobotTokenTypes.SYNTAX_MARKER))
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new SectionCompletionProvider());
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andNot(PlatformPatterns.psiComment())
                               .andNot(withTagsWithoutCodeCompletionSupport())
                               .andNot(PlatformPatterns.psiElement(RobotStubTokenTypes.ARGUMENT))
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new SettingsKeywordCompletionProvider());
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andNot(PlatformPatterns.psiComment())
                               .andNot(withTagsWithoutCodeCompletionSupport())
                               .andNot(PlatformPatterns.psiElement(RobotStubTokenTypes.ARGUMENT))
                               .andNot(PlatformPatterns.psiElement(RobotTokenTypes.PARAMETER))
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new BracketSettingsCompletionProvider());
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andNot(PlatformPatterns.psiComment())
                               .andNot(withTagsWithoutCodeCompletionSupport())
                               .and(PlatformPatterns.psiElement(RobotStubTokenTypes.ARGUMENT)
                                                    .withSuperParent(2, PlatformPatterns.psiElement(RobotTokenTypes.IMPORT)))
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new ImportCompletionProvider());
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andNot(PlatformPatterns.psiComment())
                               .andNot(withTagsWithoutCodeCompletionSupport())
                               .andNot(PlatformPatterns.psiElement(RobotStubTokenTypes.ARGUMENT))
                               .andNot(PlatformPatterns.psiElement(RobotTokenTypes.PARAMETER))
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new GherkinCompletionProvider());
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andNot(withTagsWithoutCodeCompletionSupport())
                               .andNot(PlatformPatterns.psiComment())
                               .andNot(withArgumentInKeywordStatement())
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new ControlStructureCompletionProvider());
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andNot(withTagsWithoutCodeCompletionSupport())
                               .andNot(PlatformPatterns.psiComment())
                               .andNot(withArgumentInKeywordStatement())
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new KeywordCompletionProvider());
        // Provide parameter completions in context of keyword statements
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andNot(PlatformPatterns.psiComment())
                               .andNot(withTagsWithoutCodeCompletionSupport())
                               .and(withArgumentInKeywordStatement())
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new KeywordParametersCompletionProvider());
        // Provide completions in context of variables or arguments
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andNot(withTagsWithoutCodeCompletionSupport())
                               .andOr(PlatformPatterns.psiElement(RobotStubTokenTypes.VARIABLE), PlatformPatterns.psiElement(RobotStubTokenTypes.ARGUMENT))
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new VariableCompletionProvider());
    }

    private static PsiElementPattern.Capture<PsiElement> withArgumentInKeywordStatement() {
        return PlatformPatterns.psiElement()
                               .andOr(PlatformPatterns.psiElement(RobotTokenTypes.PARAMETER), PlatformPatterns.psiElement(RobotStubTokenTypes.ARGUMENT))
                               .withAncestor(3, PlatformPatterns.psiElement(RobotStubTokenTypes.KEYWORD_STATEMENT));
    }

    private static PsiElementPattern.Capture<PsiElement> withTagsWithoutCodeCompletionSupport() {
        return PlatformPatterns.psiElement(RobotStubTokenTypes.ARGUMENT)
                               .withAncestor(2,
                                             PlatformPatterns.psiElement(RobotTokenTypes.BRACKET_SETTING).withName("[Documentation]", "[Tags]", "[Arguments]"));
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        // debugging point
        super.fillCompletionVariants(parameters, result);
    }
}
