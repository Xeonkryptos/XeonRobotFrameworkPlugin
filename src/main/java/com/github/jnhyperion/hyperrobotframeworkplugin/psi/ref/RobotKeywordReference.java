package com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.RobotTailTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.completion.RobotCompletionContributor;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.config.RobotOptionsProvider;
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
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.jetbrains.python.psi.PyDecorator;
import com.jetbrains.python.psi.PyDecoratorList;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyFunction;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class RobotKeywordReference extends PsiPolyVariantReferenceBase<KeywordInvokable> {

    public RobotKeywordReference(@NotNull KeywordInvokable keyword) {
        super(keyword, false);
    }

    @Override
    public @Nullable PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length > 0 ? resolveResults[0].getElement() : null;
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        KeywordInvokable keywordInvokable = getElement();
        PsiElement referencedSourceElement = ResolverUtils.findKeywordElement(keywordInvokable.getPresentableText(), keywordInvokable.getContainingFile());
        if (referencedSourceElement != null) {
            PyFunction pyFunction = (PyFunction) referencedSourceElement;
            PyDecoratorList decoratorList = pyFunction.getDecoratorList();
            if (decoratorList != null) {
                for (PyDecorator decorator : decoratorList.getDecorators()) {
                    String decoratorName = decorator.getName();
                    if (Objects.equals(decoratorName, "keyword")) {
                        PyExpression keywordArgumentExpression = decorator.getArgument(0, "name", PyExpression.class);
                        if (keywordArgumentExpression != null) {
                            return new ResolveResult[] { new PsiElementResolveResult(referencedSourceElement), new PsiElementResolveResult(keywordArgumentExpression) };
                        }
                    }
                }
            }
            return new ResolveResult[] { new PsiElementResolveResult(referencedSourceElement) };
        }
        return ResolveResult.EMPTY_ARRAY;
    }

    @Override
    public Object @NotNull [] getVariants() {
        String keywordPrefix = getElement().getPresentableText().split("\\.")[0];
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

                            lookupElement = RobotCompletionContributor.addReferenceType(definedKeyword.reference(), lookupElement);

                            String keywordArguments = RobotCompletionContributor.getKeywordArguments(definedKeyword);
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
