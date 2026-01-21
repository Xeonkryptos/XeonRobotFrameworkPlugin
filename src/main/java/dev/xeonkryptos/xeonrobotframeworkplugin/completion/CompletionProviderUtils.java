package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RecommendationWord;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotKeywordProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.LookupElementMarker;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.LookupElementUtil;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

final class CompletionProviderUtils {

    private CompletionProviderUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    static RobotSection getSection(PsiElement current) {
        if (current == null) {
            return null;
        }
        return PsiTreeUtil.getParentOfType(current, RobotSection.class, false);
    }

    static void addSyntaxLookup(@NotNull IElementType elementType, @NotNull CompletionResultSet resultSet) {
        List<LookupElement> lookupElements = computeAdditionalSyntaxLookups(elementType);
        resultSet.addAllElements(lookupElements);
    }

    static List<LookupElement> computeAdditionalSyntaxLookups(@NotNull IElementType type) {
        List<LookupElement> results = new ArrayList<>();
        Collection<RecommendationWord> words = RobotKeywordProvider.getRecommendationsForType(type);
        for (RecommendationWord word : words) {
            String lookupString = word.presentation();
            LookupElement element = createLookupElementForSyntaxLookup(word, (context, item) -> {
                if (type == RobotTypes.SECTION) {
                    Editor editor = context.getEditor();
                    Document document = context.getDocument();
                    int startOffset = context.getStartOffset();
                    int lineNumber = document.getLineNumber(startOffset);
                    int lineStartOffset = document.getLineStartOffset(lineNumber);
                    int lineEndOffset = document.getLineEndOffset(lineNumber);

                    int newEndOffset = lineStartOffset + lookupString.length();
                    document.replaceString(lineStartOffset, lineEndOffset, lookupString);
                    editor.getCaretModel().moveToOffset(newEndOffset);
                    context.setTailOffset(newEndOffset);
                }
            });
            results.add(element);
        }
        return results;
    }

    static LookupElement createLookupElementForSyntaxLookup(RecommendationWord word, @Nullable InsertHandler<LookupElement> insertHandler) {
        String text = word.lookup();
        String lookupString = word.presentation();
        String[] lookupStrings = { text, WordUtils.capitalize(text), lookupString, WordUtils.capitalize(lookupString), lookupString.toLowerCase() };
        return TailTypeDecorator.withTail(LookupElementBuilder.create(lookupString)
                                                                               .withLookupStrings(Arrays.asList(lookupStrings))
                                                                               .withPresentableText(lookupString)
                                                                               .withInsertHandler(insertHandler)
                                                                               .withCaseSensitivity(true), word.tailType());
    }

    static Optional<LookupElement> createLookupElement(LookupElementMarker lookupElementMarker, @Nullable Icon icon, boolean bold, @NotNull TailType tailType) {
        String lookup = lookupElementMarker.getLookup();
        if (lookup != null) {
            String[] lookupWords = lookupElementMarker.getLookupWords();
            List<String> lookupStrings = Arrays.asList(lookupWords);
            LookupElementBuilder builder = LookupElementBuilder.create(lookup)
                                                               .withLookupStrings(lookupStrings)
                                                               .withIcon(icon)
                                                               .withPsiElement(lookupElementMarker.reference())
                                                               .withCaseSensitivity(lookupElementMarker.isCaseSensitive())
                                                               .withPresentableText(lookupElementMarker.getPresentableText())
                                                               .withInsertHandler(lookupElementMarker.getInsertHandler());
            if (bold) {
                builder = builder.bold();
            }
            builder = LookupElementUtil.addReferenceType(lookupElementMarker.reference(), builder);
            TailTypeDecorator<LookupElementBuilder> lookupElement = new TailTypeDecorator<>(builder) {

                @NotNull
                @Override
                protected TailType computeTailType(InsertionContext context) {
                    return tailType;
                }

                @Override
                public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {
                    getDelegate().putUserData(key, value);
                    super.putUserData(key, value);
                }
            };
            return Optional.of(lookupElement);
        }
        return Optional.empty();
    }
}
