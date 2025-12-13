package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.TailTypes;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.GlobalConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public enum KeywordCompletionModification {

    NONE {
        @Nullable
        @Override
        public Character getIdentifier() {
            return null;
        }

        @Override
        public TailTypeDecorator<LookupElementBuilder> createTail(LookupElementBuilder decoratedElement,
                                                                  DefinedKeyword keyword,
                                                                  Collection<DefinedParameter> alreadyAddedParameters) {
            return TailTypeDecorator.withTail(decoratedElement, TailTypes.noneType());
        }
    }, ONLY_MANDATORY {
        @NotNull
        @Override
        public Character getIdentifier() {
            return '*';
        }

        @Override
        public TailTypeDecorator<LookupElementBuilder> createTail(LookupElementBuilder decoratedElement,
                                                                  DefinedKeyword keyword,
                                                                  Collection<DefinedParameter> alreadyAddedParameters) {
            return TailTypeDecorator.withTail(decoratedElement, new TailType() {
                @Override
                public int processTail(Editor editor, int tailOffset) {
                    Document document = editor.getDocument();
                    String prefixIndentationText = computeParameterTailBaseText(keyword, editor, tailOffset);

                    int currentOffset = tailOffset;
                    int addedOffset = 0;
                    for (DefinedParameter parameter : keyword.getParameters()) {
                        if (!parameter.hasDefaultValue() && !alreadyAddedParameters.contains(parameter)) {
                            String parameterInsertString = prefixIndentationText + parameter.getLookup() + "=";
                            document.insertString(currentOffset, parameterInsertString);
                            currentOffset += parameterInsertString.length();
                            addedOffset += parameterInsertString.length();
                        }
                    }
                    return moveCaret(editor, tailOffset, addedOffset);
                }
            });
        }
    }, ALL {
        @NotNull
        @Override
        public Character getIdentifier() {
            return '/';
        }

        @Override
        public TailTypeDecorator<LookupElementBuilder> createTail(LookupElementBuilder decoratedElement,
                                                                  DefinedKeyword keyword,
                                                                  Collection<DefinedParameter> alreadyAddedParameters) {
            return TailTypeDecorator.withTail(decoratedElement, new TailType() {
                @Override
                public int processTail(Editor editor, int tailOffset) {
                    Document document = editor.getDocument();
                    String prefixIndentationText = computeParameterTailBaseText(keyword, editor, tailOffset);

                    int currentOffset = tailOffset;
                    int addedOffset = 0;
                    for (DefinedParameter parameter : keyword.getParameters()) {
                        if (alreadyAddedParameters.contains(parameter)) {
                            continue;
                        }
                        String parameterInsertString = prefixIndentationText + parameter.getLookup() + "=";
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
        }
    };

    @Nullable
    public abstract Character getIdentifier();

    public abstract TailTypeDecorator<LookupElementBuilder> createTail(LookupElementBuilder decoratedElement,
                                                                       DefinedKeyword keyword,
                                                                       Collection<DefinedParameter> alreadyAddedParameters);

    private static String computeParameterTailBaseText(DefinedKeyword keyword, Editor editor, int tailOffset) {
        String keywordName = keyword.getKeywordName();
        Document document = editor.getDocument();

        int lineNumber = document.getLineNumber(tailOffset);
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        int keywordStartOffset = tailOffset - keywordName.length();
        String textBeforeKeyword = document.getText(new TextRange(lineStartOffset, keywordStartOffset));
        int numberOfWhitespaceCharacters = textBeforeKeyword.length() - textBeforeKeyword.stripLeading().length();
        String indentationWhitespace = " ".repeat(numberOfWhitespaceCharacters);
        return "\n" + indentationWhitespace + GlobalConstants.CONTINUATION + GlobalConstants.DEFAULT_INDENTATION;
    }

    public static boolean isKeywordStartsWithModifier(String keywordName) {
        if (keywordName == null || keywordName.isEmpty()) {
            return false;
        }
        return Arrays.stream(values())
                     .map(KeywordCompletionModification::getIdentifier)
                     .filter(Objects::nonNull)
                     .map(Object::toString)
                     .anyMatch(keywordName::startsWith);
    }
}
