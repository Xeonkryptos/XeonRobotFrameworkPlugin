package com.github.jnhyperion.hyperrobotframeworkplugin.ide.completion;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.LookupElementUtil;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.config.RobotOptionsProvider;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.ImportType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedKeyword;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedParameter;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Heading;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFile;
import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.TailTypes;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

class KeywordCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        if (CompletionProviderUtils.isIndexPositionAWhitespaceCharacter(parameters)) {
            Heading heading = CompletionProviderUtils.getHeading(parameters.getPosition());
            if (heading != null && (heading.containsTestCases() || heading.containsKeywordDefinitions() || heading.containsTasks())) {
                boolean startingWithSlash = isStartingWithSlash(parameters);
                addDefinedKeywordsFromFile(result, parameters.getOriginalFile(), startingWithSlash);
            }
        }
    }

    private boolean isStartingWithSlash(@NotNull CompletionParameters parameters) {
        int offset = parameters.getOffset();

        Document document = parameters.getEditor().getDocument();
        int lineNumber = document.getLineNumber(offset);
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        String textBeforeOffset = document.getText(new TextRange(lineStartOffset, offset));
        if (textBeforeOffset.trim().isEmpty()) {
            return false;
        }
        int firstCharacterInLine = textBeforeOffset.trim().codePointAt(0);
        return "/".equals(Character.toString(firstCharacterInLine));
    }

    private void addDefinedKeywordsFromFile(CompletionResultSet resultSet, PsiFile file, boolean addKeywordParametersOnInsert) {
        if (file instanceof RobotFile robotFile) {
            boolean capitalizeKeywords = RobotOptionsProvider.getInstance(robotFile.getProject()).capitalizeKeywords();
            addDefinedKeywords(robotFile.getDefinedKeywords(), resultSet, capitalizeKeywords, addKeywordParametersOnInsert).forEach(lookupElement -> {
                lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.KEYWORDS);
                lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.KEYWORD);
            });
            boolean allowTransitiveImports = RobotOptionsProvider.getInstance(file.getProject()).allowTransitiveImports();
            for (KeywordFile importedFile : robotFile.getImportedFiles(allowTransitiveImports)) {
                if (importedFile.getImportType() != ImportType.VARIABLES) {
                    addDefinedKeywords(importedFile.getDefinedKeywords(),
                                       resultSet,
                                       capitalizeKeywords,
                                       addKeywordParametersOnInsert).forEach(lookupElement -> {
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
                                                                boolean addKeywordParametersOnInsert) {
        List<LookupElement> lookupElements = new ArrayList<>();
        for (DefinedKeyword keyword : keywords) {
            String keywordName = keyword.getKeywordName();
            String displayName = capitalize ? WordUtils.capitalize(keywordName) : keywordName;
            String[] lookupStrings = new String[] { keywordName, WordUtils.capitalize(keywordName), keywordName.toLowerCase() };
            lookupStrings = Arrays.stream(lookupStrings).map(lookup -> "/" + lookup).toArray(String[]::new);
            LookupElementBuilder lookupElement = LookupElementBuilder.create(displayName)
                                                                     .withLookupStrings(Arrays.asList(lookupStrings))
                                                                     .withPresentableText(displayName)
                                                                     .withCaseSensitivity(true)
                                                                     .withIcon(Nodes.Function);
            LookupElementBuilder decoratedElement = LookupElementUtil.addReferenceType(keyword.reference(), lookupElement);
            displayName = keyword.getArgumentsDisplayable();
            if (displayName != null) {
                decoratedElement = decoratedElement.withTailText(displayName);
            }

            TailTypeDecorator<LookupElementBuilder> tailTypeDecorator;
            if (keyword.hasParameters()) {
                if (addKeywordParametersOnInsert) {
                    tailTypeDecorator = TailTypeDecorator.withTail(decoratedElement, new TailType() {
                        @Override
                        public int processTail(Editor editor, int tailOffset) {
                            Document document = editor.getDocument();

                            int lineNumber = document.getLineNumber(tailOffset);
                            int lineStartOffset = document.getLineStartOffset(lineNumber);
                            int keywordStartOffset = tailOffset - keywordName.length();
                            String spaceBeforeOffset = document.getText(new TextRange(lineStartOffset, keywordStartOffset));

                            int currentOffset = tailOffset;
                            int addedOffset = 0;
                            for (DefinedParameter parameter : keyword.getParameters()) {
                                String parameterInsertString = "\n" + spaceBeforeOffset + "...    " + parameter.getLookup() + "=";
                                if (parameter.hasDefaultValue()) {
                                    parameterInsertString += parameter.getDefaultValue();
                                }
                                document.insertString(currentOffset, parameterInsertString);
                                currentOffset += parameterInsertString.length();
                                addedOffset += parameterInsertString.length();
                            }
                            return moveCaret(editor, tailOffset, addedOffset);
                        }
                    });
                } else {
                    tailTypeDecorator = TailTypeDecorator.withTail(decoratedElement, TailTypes.noneType());
                }
            } else {
                tailTypeDecorator = TailTypeDecorator.withTail(decoratedElement, TailTypes.noneType());
            }
            resultSet.addElement(tailTypeDecorator);
            lookupElements.add(tailTypeDecorator);
        }
        return lookupElements;
    }
}
