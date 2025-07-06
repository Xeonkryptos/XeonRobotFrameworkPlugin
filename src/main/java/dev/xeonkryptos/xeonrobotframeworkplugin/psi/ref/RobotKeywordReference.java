package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.codeInsight.TailTypes;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.RobotTailTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion.KeywordCompletionModification;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallId;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.LookupElementUtil;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RobotKeywordReference extends PsiReferenceBase<RobotKeywordCallId> {

    public RobotKeywordReference(@NotNull RobotKeywordCallId keyword) {
        super(keyword, false);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        RobotKeywordCallId keywordInvokable = getElement();
        ResolveCache resolveCache = ResolveCache.getInstance(keywordInvokable.getProject());
        return resolveCache.resolveWithCaching(this, (robotKeywordReference, incompleteCode) -> {
            String keywordInvokableName = keywordInvokable.getName();
            if (KeywordCompletionModification.isKeywordStartsWithModifier(keywordInvokableName)) {
                keywordInvokableName = keywordInvokableName.substring(1);
            }
            PsiFile containingFile = keywordInvokable.getContainingFile();
            return ResolverUtils.findKeywordReference(keywordInvokableName, containingFile);
        }, false, false);
    }

    @Override
    public Object @NotNull [] getVariants() {
        String name = getElement().getName();
        String keywordPrefix = name.split("\\.")[0];
        PsiFile containingFile = getElement().getContainingFile();

        if (containingFile instanceof RobotFile robotFile) {
            boolean capitalizeKeywords = RobotOptionsProvider.getInstance(containingFile.getProject()).capitalizeKeywords();
            for (KeywordFile keywordFile : robotFile.getImportedFiles(true)) {
                if (keywordFile.getImportType() == ImportType.LIBRARY && keywordFile.isDifferentNamespace()) {
                    String libraryName = keywordFile.toString();
                    if (keywordPrefix.equalsIgnoreCase(libraryName)) {
                        Collection<DefinedKeyword> definedKeywords = keywordFile.getDefinedKeywords();
                        List<TailTypeDecorator<LookupElementBuilder>> tailTypeDecorators = new ArrayList<>();

                        for (DefinedKeyword definedKeyword : definedKeywords) {
                            String keywordName = capitalizeKeywords ? WordUtils.capitalize(definedKeyword.getKeywordName()) : definedKeyword.getKeywordName();
                            String fullKeywordName = (capitalizeKeywords ? WordUtils.capitalize(libraryName) : libraryName) + "." + keywordName;
                            String[] lookupStrings = { fullKeywordName,
                                                       WordUtils.capitalize(fullKeywordName),
                                                       fullKeywordName.toLowerCase(),
                                                       fullKeywordName.toUpperCase() };

                            LookupElementBuilder lookupElement = LookupElementBuilder.create(fullKeywordName)
                                                                                     .withLookupStrings(Arrays.asList(lookupStrings))
                                                                                     .withPresentableText(keywordName)
                                                                                     .withCaseSensitivity(true)
                                                                                     .withIcon(Nodes.Function);

                            lookupElement = LookupElementUtil.addReferenceType(definedKeyword.reference(), lookupElement);

                            String keywordArguments = definedKeyword.getArgumentsDisplayable();
                            if (keywordArguments != null) {
                                lookupElement = lookupElement.withTailText(keywordArguments);
                            }

                            TailTypeDecorator<LookupElementBuilder> tailTypeDecorator = TailTypeDecorator.withTail(lookupElement,
                                                                                                                   definedKeyword.hasParameters() ?
                                                                                                                   RobotTailTypes.TAB :
                                                                                                                   TailTypes.noneType());
                            tailTypeDecorators.add(tailTypeDecorator);
                        }

                        return tailTypeDecorators.toArray();
                    }
                }
            }
        }
        return EMPTY_ARRAY;
    }
}
