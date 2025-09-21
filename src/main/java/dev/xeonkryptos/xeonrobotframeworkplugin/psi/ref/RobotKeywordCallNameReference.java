package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.codeInsight.TailTypes;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.psi.PsiElement;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
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
            PsiElement[] keywordReferences = findKeywordReferences(keywordCallName, containingFile);
            return Arrays.stream(keywordReferences).map(PsiElementResolveResult::new).toArray(ResolveResult[]::new);
        }, false, false);
    }

    @NotNull
    private PsiElement[] findKeywordReferences(@NotNull RobotKeywordCallName keywordCallName, @Nullable PsiFile psiFile) {
        RobotKeywordCallLibrary keywordCallLibrary = keywordCallName.getKeywordCallLibrary();
        String libraryName = keywordCallLibrary != null ? keywordCallLibrary.getText() : null;
        String keywordName = keywordCallName.getText();
        if (KeywordCompletionModification.isKeywordStartsWithModifier(libraryName)) {
            libraryName = libraryName.substring(1);
        } else if (libraryName == null && KeywordCompletionModification.isKeywordStartsWithModifier(keywordName)) {
            keywordName = keywordName.substring(1);
        }
        if (libraryName != null) {
            int libraryNameLength = libraryName.length();
            keywordName = keywordName.substring(libraryNameLength + 1);
        }
        return findKeywordReferences(libraryName, keywordName, psiFile);
    }

    @NotNull
    private PsiElement[] findKeywordReferences(@Nullable String libraryName, @NotNull String keyword, @Nullable PsiFile psiFile) {
        if (!(psiFile instanceof RobotFile robotFile)) {
            return null;
        }

        Collection<PsiElement> keywordElements = new LinkedHashSet<>();
        for (DefinedKeyword definedKeyword : robotFile.getDefinedKeywords()) {
            if (definedKeyword.matches(keyword)) {
                keywordElements.add(definedKeyword.reference());
            }
        }

        Collection<KeywordFile> importedFiles;
        if (libraryName != null) {
            importedFiles = robotFile.findImportedFilesWithLibraryName(libraryName);
        } else {
            boolean includeTransitive = RobotOptionsProvider.getInstance(psiFile.getProject()).allowTransitiveImports();
            importedFiles = robotFile.collectImportedFiles(includeTransitive);
        }

        for (KeywordFile keywordFile : importedFiles) {
            for (DefinedKeyword definedKeyword : keywordFile.getDefinedKeywords()) {
                if (definedKeyword.matches(keyword)) {
                    keywordElements.add(definedKeyword.reference());
                }
            }
        }
        return keywordElements.toArray(PsiElement[]::new);
    }

    @Override
    public Object @NotNull [] getVariants() {
        RobotKeywordCallName keywordCallName = getElement();
        RobotKeywordCallLibrary keywordCallLibrary = keywordCallName.getKeywordCallLibrary();
        String libraryName = keywordCallLibrary != null ? keywordCallLibrary.getText() : null;
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
