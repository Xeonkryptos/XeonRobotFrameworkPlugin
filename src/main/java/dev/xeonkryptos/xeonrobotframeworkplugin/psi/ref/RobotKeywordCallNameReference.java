package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.codeInsight.TailTypes;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.RobotTailTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion.KeywordCompletionModification;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallLibrary;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCallName;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.LookupElementUtil;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RobotKeywordCallNameReference extends PsiPolyVariantReferenceBase<RobotKeywordCallName> {

    public RobotKeywordCallNameReference(@NotNull RobotKeywordCallName keyword) {
        super(keyword, false);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        RobotKeywordCallName keywordCallName = getElement();
        ResolveCache resolveCache = ResolveCache.getInstance(keywordCallName.getProject());
        return resolveCache.resolveWithCaching(this, (robotKeywordReference, incompCode) -> {
            PsiFile containingFile = keywordCallName.getContainingFile();
            return Arrays.stream(ResolverUtils.findKeywordReferences(keywordCallName, containingFile))
                         .map(PsiElementResolveResult::new)
                         .toArray(ResolveResult[]::new);
        }, false, false);
    }

    @Override
    public Object @NotNull [] getVariants() {
        RobotKeywordCallName keywordCallName = getElement();
        RobotKeywordCallLibrary keywordCallLibrary = keywordCallName.getKeywordCallLibrary();
        String libraryName = keywordCallLibrary != null ? keywordCallLibrary.getName() : null;
        PsiFile containingFile = keywordCallName.getContainingFile();
        if (containingFile instanceof RobotFile robotFile) {
            if (KeywordCompletionModification.isKeywordStartsWithModifier(libraryName)) {
                libraryName = libraryName.substring(1);
            }
            RobotOptionsProvider optionsProvider = RobotOptionsProvider.getInstance(containingFile.getProject());
            boolean capitalizeKeywords = optionsProvider.capitalizeKeywords();
            boolean transitiveImports = optionsProvider.allowTransitiveImports();
            for (KeywordFile keywordFile : robotFile.collectImportedFiles(transitiveImports)) {
                if (keywordFile.getImportType() == ImportType.LIBRARY) {
                    String currentLibraryName = keywordFile.getLibraryName();
                    if (libraryName == null || libraryName.equalsIgnoreCase(currentLibraryName)) {
                        Collection<DefinedKeyword> definedKeywords = keywordFile.getDefinedKeywords();
                        List<TailTypeDecorator<LookupElementBuilder>> tailTypeDecorators = new ArrayList<>();

                        for (DefinedKeyword definedKeyword : definedKeywords) {
                            String keywordName = capitalizeKeywords ? WordUtils.capitalize(definedKeyword.getKeywordName()) : definedKeyword.getKeywordName();
                            String fullKeywordName = (capitalizeKeywords ? WordUtils.capitalize(currentLibraryName) : currentLibraryName) + "." + keywordName;
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
