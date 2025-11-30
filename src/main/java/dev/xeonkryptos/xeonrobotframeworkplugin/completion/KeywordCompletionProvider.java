package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

import com.intellij.codeInsight.TailTypes;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.intellij.util.Processor;
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.index.PyRobotKeywordDefinitionIndex.Util;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ParameterDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.KeywordDefinitionNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotUserKeywordsCollector;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.LookupElementUtil;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
                                                      .filter(modification -> modification.getIdentifier() != null
                                                                              && firstCharacter == modification.getIdentifier())
                                                      .findFirst()
                                                      .orElse(KeywordCompletionModification.NONE);
            }
            Collection<DefinedParameter> alreadyAddedParameters = keywordCall.getParameterList()
                                                                             .stream()
                                                                             .map(param -> new ParameterDto(param, param.getParameterName(), null))
                                                                             .collect(Collectors.toSet());

            Collection<LookupElement> lookupElements = new LinkedList<>();
            addDefinedKeywordsFromFile(lookupElements, parameters.getOriginalFile(), keywordCompletionModification, alreadyAddedParameters);
            result.addAllElements(lookupElements);
        }
    }

    private void addDefinedKeywordsFromFile(Collection<LookupElement> lookupElements,
                                            PsiFile file,
                                            KeywordCompletionModification keywordCompletionModification,
                                            Collection<DefinedParameter> alreadyAddedParameters) {
        RobotFile robotFile = (RobotFile) file;
        RobotOptionsProvider robotOptionsProvider = RobotOptionsProvider.getInstance(robotFile.getProject());
        boolean capitalizeKeywords = robotOptionsProvider.capitalizeKeywords();

        Collection<DefinedKeyword> definedKeywordsFromRobotFile = collectDefinedKeywords(robotFile);
        addDefinedKeywords(definedKeywordsFromRobotFile, capitalizeKeywords, keywordCompletionModification, alreadyAddedParameters).forEach(lookupElement -> {
            lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.KEYWORDS);
            lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.KEYWORD);
            lookupElements.add(lookupElement);
        });
    }

    private Collection<DefinedKeyword> collectDefinedKeywords(RobotFile robotFile) {
        Collection<VirtualFile> importedFiles = collectImportedVirtualFilesOneselfIncluded(robotFile);

        Project project = robotFile.getProject();
        GlobalSearchScope searchScope = GlobalSearchScope.filesWithLibrariesScope(project, importedFiles);

        Set<DefinedKeyword> definedKeywords = new LinkedHashSet<>();
        collectUserKeywords(project, searchScope, definedKeywords);

        Collection<DefinedKeyword> constructedPythonKeywords = Util.getKeywordNames(project, searchScope, null);
        definedKeywords.addAll(constructedPythonKeywords);
        return definedKeywords;
    }

    private static @NotNull Collection<VirtualFile> collectImportedVirtualFilesOneselfIncluded(RobotFile robotFile) {
        Collection<VirtualFile> importedFiles = robotFile.collectImportedFiles(true, ImportType.LIBRARY)
                                                         .stream()
                                                         .map(KeywordFile::getVirtualFile)
                                                         .collect(Collectors.toSet());
        VirtualFile virtualFile = robotFile.getVirtualFile();
        if (virtualFile == null) {
            virtualFile = robotFile.getOriginalFile().getVirtualFile();
        }
        importedFiles.add(virtualFile);
        return importedFiles;
    }

    private static void collectUserKeywords(Project project, GlobalSearchScope searchScope, Set<DefinedKeyword> definedKeywords) {
        Processor<String> userKeywordProcessor = normalizedKeywordName -> {
            for (RobotUserKeywordStatement userKeywordStatement : KeywordDefinitionNameIndex.getUserKeywordStatements(normalizedKeywordName,
                                                                                                                      project,
                                                                                                                      searchScope)) {
                RobotUserKeywordsCollector robotUserKeywordsCollector = new RobotUserKeywordsCollector();
                userKeywordStatement.accept(robotUserKeywordsCollector);
                Collection<DefinedKeyword> constructedUserKeywords = robotUserKeywordsCollector.getKeywords();
                definedKeywords.addAll(constructedUserKeywords);
            }
            return true;
        };
        StubIndex.getInstance().processAllKeys(KeywordDefinitionNameIndex.KEY, userKeywordProcessor, searchScope);
    }

    private Collection<LookupElement> addDefinedKeywords(Collection<DefinedKeyword> keywords,
                                                         boolean capitalize,
                                                         KeywordCompletionModification keywordCompletionModification,
                                                         Collection<DefinedParameter> alreadyAddedParameters) {
        List<LookupElement> lookupElementKeywords = new ArrayList<>();
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
            lookupElementKeywords.add(tailTypeDecorator);
        }
        return lookupElementKeywords;
    }
}
