package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.LookupElementUtil;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.RobotTailTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.config.RobotOptionsProvider;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ImportModificationTracker;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.ImportType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedKeyword;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordInvokable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFile;
import com.intellij.codeInsight.TailTypes;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.CachedValueProvider.Result;
import com.intellij.psi.util.CachedValuesManager;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RobotKeywordReference extends PsiReferenceBase<KeywordInvokable> {

    public RobotKeywordReference(@NotNull KeywordInvokable keyword) {
        super(keyword, false);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        KeywordInvokable keywordInvokable = getElement();
        return CachedValuesManager.getCachedValue(keywordInvokable, () -> {
            String keywordInvokableName = keywordInvokable.getName();
            PsiFile containingFile = keywordInvokable.getContainingFile();
            PsiElement reference = ResolverUtils.findKeywordReference(keywordInvokableName, containingFile);
            return new Result<>(reference, keywordInvokable, ImportModificationTracker.getInstance());
        });
    }

    @Override
    public Object @NotNull [] getVariants() {
        String name = getElement().getName();
        assert name != null;
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
