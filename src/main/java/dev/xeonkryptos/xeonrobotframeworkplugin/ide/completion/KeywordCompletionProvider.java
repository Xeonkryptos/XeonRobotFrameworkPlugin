package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

import com.intellij.codeInsight.TailTypes;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ParameterDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotKeywordCallHolderSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.LookupElementUtil;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class KeywordCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        if (CompletionProviderUtils.isIndexPositionAWhitespaceCharacter(parameters)) {
            PsiElement position = parameters.getPosition();
            RobotSection section = CompletionProviderUtils.getSection(position);
            if (section == null) {
                return;
            }

            RobotKeywordCallHolderSection keywordCallHolderSection = new RobotKeywordCallHolderSection();
            section.accept(keywordCallHolderSection);
            if (keywordCallHolderSection.isKeywordCallHolderSection()) {
                PsiElement positionContext = position.getContext();
                RobotKeywordCall keywordCall = PsiTreeUtil.getParentOfType(positionContext, RobotKeywordCall.class);
                if (keywordCall == null) {
                    return;
                }

                String name = keywordCall.getName();
                KeywordCompletionModification keywordCompletionModification = KeywordCompletionModification.NONE;
                if (!name.isEmpty()) {
                    char firstCharacter = name.charAt(0);
                    keywordCompletionModification = Arrays.stream(KeywordCompletionModification.values())
                                                          .filter(modification -> modification.getIdentifier() != null)
                                                          .filter(modification -> firstCharacter == modification.getIdentifier())
                                                          .findFirst()
                                                          .orElse(KeywordCompletionModification.NONE);
                }
                Collection<DefinedParameter> alreadyAddedParameters = keywordCall.getParameterList()
                                                                                 .stream()
                                                                                 .map(param -> new ParameterDto(param, param.getName(), null))
                                                                                 .collect(Collectors.toSet());

                addDefinedKeywordsFromFile(result, parameters.getOriginalFile(), keywordCompletionModification, alreadyAddedParameters);
            }
        }
    }

    private void addDefinedKeywordsFromFile(CompletionResultSet resultSet,
                                            PsiFile file,
                                            KeywordCompletionModification keywordCompletionModification,
                                            Collection<DefinedParameter> alreadyAddedParameters) {
        if (file instanceof RobotFile robotFile) {
            RobotOptionsProvider robotOptionsProvider = RobotOptionsProvider.getInstance(robotFile.getProject());
            boolean capitalizeKeywords = robotOptionsProvider.capitalizeKeywords();
            boolean allowTransitiveImports = robotOptionsProvider.allowTransitiveImports();

            Collection<DefinedKeyword> definedKeywordsFromRobotFile = robotFile.getDefinedKeywords();
            addDefinedKeywords(definedKeywordsFromRobotFile, resultSet, capitalizeKeywords, keywordCompletionModification, alreadyAddedParameters).forEach(
                    lookupElement -> {
                        lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.KEYWORDS);
                        lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.KEYWORD);
                    });
            for (KeywordFile importedFile : robotFile.collectImportedFiles(allowTransitiveImports)) {
                if (importedFile.getImportType() != ImportType.VARIABLES) {
                    Collection<DefinedKeyword> definedKeywordsFromImportedFile = importedFile.getDefinedKeywords();
                    addDefinedKeywords(definedKeywordsFromImportedFile,
                                       resultSet,
                                       capitalizeKeywords,
                                       keywordCompletionModification,
                                       alreadyAddedParameters).forEach(lookupElement -> {
                        lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.KEYWORDS);
                        lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.KEYWORD);
                    });
                }
            }
        }
    }

    private Collection<LookupElement> addDefinedKeywords(Collection<DefinedKeyword> keywords,
                                                         CompletionResultSet resultSet,
                                                         boolean capitalize,
                                                         KeywordCompletionModification keywordCompletionModification,
                                                         Collection<DefinedParameter> alreadyAddedParameters) {
        List<LookupElement> lookupElements = new ArrayList<>();
        for (DefinedKeyword keyword : keywords) {
            String keywordName = keyword.getKeywordName();
            String displayName = capitalize ? WordUtils.capitalize(keywordName) : keywordName;
            String libraryName = keyword.getLibraryName();
            if (libraryName != null) {
                displayName = libraryName + "." + displayName;
            }
            List<String> lookupStrings = Stream.of(keywordName, WordUtils.capitalize(keywordName), keywordName.toLowerCase()).flatMap(lookup -> {
                Character identifier = keywordCompletionModification.getIdentifier();
                if (libraryName != null) {
                    return Stream.of(identifier + libraryName + "." + lookup, identifier + lookup);
                }
                return Stream.of(identifier + lookup);
            }).toList();
            LookupElementBuilder lookupElement = LookupElementBuilder.create(displayName)
                                                                     .withLookupStrings(lookupStrings)
                                                                     .withPresentableText(displayName)
                                                                     .withCaseSensitivity(true)
                                                                     .withIcon(Nodes.Function)
                                                                     .withStrikeoutness(keyword.isDeprecated());
            LookupElementBuilder decoratedElement = LookupElementUtil.addReferenceType(keyword.reference(), lookupElement);
            displayName = keyword.getArgumentsDisplayable();
            if (displayName != null) {
                decoratedElement = decoratedElement.withTailText(displayName);
            }

            TailTypeDecorator<LookupElementBuilder> tailTypeDecorator;
            if (keyword.hasParameters()) {
                tailTypeDecorator = keywordCompletionModification.createTail(decoratedElement, keyword, alreadyAddedParameters);
            } else {
                tailTypeDecorator = TailTypeDecorator.withTail(decoratedElement, TailTypes.noneType());
            }
            resultSet.addElement(tailTypeDecorator);
            lookupElements.add(tailTypeDecorator);
        }
        return lookupElements;
    }
}
