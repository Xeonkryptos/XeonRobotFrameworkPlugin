package com.github.jnhyperion.hyperrobotframeworkplugin.ide;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.config.RobotOptionsProvider;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.icons.RobotIcons;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RecommendationWord;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotElementType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotKeywordProvider;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotResourceFileType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.ImportType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.KeywordDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Argument;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedKeyword;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedVariable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Heading;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Import;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinition;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinitionImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordInvokable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.RobotStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.ref.RobotFileManager;
import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.TailTypes;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.icons.AllIcons;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.python.psi.PyParameter;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RobotCompletionContributor extends CompletionContributor {

    public RobotCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().inFile(PlatformPatterns.psiElement(RobotFile.class)), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                if (!(parameters.getOriginalPosition() instanceof PsiComment)) {
                    if (RobotCompletionContributor.isValidCompletion(parameters) &&
                        !RobotCompletionContributor.isArgument(parameters.getOriginalPosition()) &&
                        !RobotCompletionContributor.isKeywordInvokable(parameters.getOriginalPosition())) {
                        boolean isResource = parameters.getOriginalFile().getFileType() instanceof RobotResourceFileType;

                        for (LookupElement element : addSyntaxLookup(RobotTokenTypes.HEADING)) {
                            if (!isResource || !"*** Test Cases ***".equals(element.getLookupString()) && !"*** Tasks ***".equals(element.getLookupString())) {
                                result.addElement(element);
                            }
                        }
                    }
                }
            }
        });
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().inFile(PlatformPatterns.psiElement(RobotFile.class)), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                if (!(parameters.getOriginalPosition() instanceof PsiComment) && !isInKeywordStatement(parameters.getPosition())) {
                    PsiElement heading = getHeading(parameters.getOriginalPosition());
                    if (!isValidCompletion(parameters) &&
                        (containsTestCases(heading) || containsKeywordDefinitions(heading)) &&
                        !isArgument(parameters.getOriginalPosition())) {
                        RobotCompletionContributor.addSyntaxLookup(RobotTokenTypes.BRACKET_SETTING, result);
                    }
                }
            }
        });
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().inFile(PlatformPatterns.psiElement(RobotFile.class)), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                if (!(parameters.getOriginalPosition() instanceof PsiComment)) {
                    PsiElement originalPosition = parameters.getOriginalPosition();
                    PsiElement heading = getHeading(originalPosition);
                    if (containsSettings(heading) && !isArgument(originalPosition) && !isKeywordInvokable(originalPosition)) {
                        addSyntaxLookup(RobotTokenTypes.SETTING, result);
                        addSyntaxLookup(RobotTokenTypes.IMPORT, result);
                    }
                }
            }
        });
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().inFile(PlatformPatterns.psiElement(RobotFile.class)), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                if (!(parameters.getOriginalPosition() instanceof PsiComment)) {
                    PsiElement originalPosition = parameters.getOriginalPosition();
                    if (originalPosition != null) {
                        PsiElement parent = originalPosition.getParent();
                        if (parent instanceof Argument) {
                            PsiElement grandParent = parent.getParent();
                            if (grandParent instanceof Import importElement) {
                                if (importElement.isLibrary()) {
                                    addBuiltinLibraryCompletions(result, parameters.getOriginalFile());
                                    if (importElement.getChildren().length > 1) {
                                        for (LookupElement lookupElement : addSyntaxLookup(RobotTokenTypes.SYNTAX_MARKER)) {
                                            if ("AS".equals(lookupElement.getLookupString())) {
                                                result.addElement(lookupElement);
                                            }
                                        }
                                    }
                                }

                                if (importElement.isResource()) {
                                    addResourceFilePaths(result, parameters.getOriginalFile());
                                }
                            }
                        }
                    }
                }
            }
        });
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().inFile(PlatformPatterns.psiElement(RobotFile.class)), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                if (!(parameters.getOriginalPosition() instanceof PsiComment) && !isInKeywordStatement(parameters.getPosition())) {
                    PsiElement heading = getHeading(parameters.getOriginalPosition());
                    if (!isValidCompletion(parameters) && containsTestCases(heading) && !isArgument(parameters.getOriginalPosition())) {
                        addSyntaxLookup(RobotTokenTypes.GHERKIN, result);
                    }
                }
            }
        });
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().inFile(PlatformPatterns.psiElement(RobotFile.class)), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                if (!(parameters.getOriginalPosition() instanceof PsiComment) && !isInKeywordStatement(parameters.getPosition())) {
                    PsiElement heading = getHeading(parameters.getOriginalPosition());
                    if (!isValidCompletion(parameters) && (containsTestCases(heading) || containsKeywordDefinitions(heading))) {
                        List<LookupElement> lookupElements = addSyntaxLookup(RobotTokenTypes.SYNTAX_MARKER);
                        List<LookupElement> nonSpecialElements = new ArrayList<>();
                        List<LookupElement> specialElements = new ArrayList<>();

                        for (LookupElement element : lookupElements) {
                            if (!element.getLookupString().equals("AS") && !element.getLookupString().startsWith("IN")) {
                                nonSpecialElements.add(element);
                            } else {
                                specialElements.add(element);
                            }
                        }

                        if (!isArgument(parameters.getOriginalPosition())) {
                            result.addAllElements(nonSpecialElements);
                            return;
                        }

                        result.addAllElements(specialElements);
                    }
                }
            }
        });
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().inFile(PlatformPatterns.psiElement(RobotFile.class)), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                if (!(parameters.getOriginalPosition() instanceof PsiComment) && !isInKeywordStatement(parameters.getPosition())) {
                    PsiElement heading = getHeading(parameters.getOriginalPosition());
                    if (!isValidCompletion(parameters) && (containsTestCases(heading) || containsKeywordDefinitions(heading) || containsTasks(heading))) {
                        addDefinedKeywordsFromFile(result, parameters.getOriginalFile());
                    }

                    if (containsSettings(heading) && isKeywordInvokable(parameters.getOriginalPosition())) {
                        addDefinedKeywordsFromFile(result, parameters.getOriginalFile());
                    }
                }
            }
        });
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().inFile(PlatformPatterns.psiElement(RobotFile.class)), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                if (!(parameters.getOriginalPosition() instanceof PsiComment)) {
                    if (isArgument(parameters.getOriginalPosition())) {
                        addDefinedVariablesFromImportedFiles(result, parameters.getOriginalFile(), parameters.getOriginalPosition());
                        addDefinedVariablesFromKeyword(result, parameters.getOriginalPosition());
                    }
                    if (isInKeywordStatement(parameters.getPosition())) {
                        KeywordStatement keyword = PsiTreeUtil.getParentOfType(parameters.getPosition(), KeywordStatement.class);
                        assert keyword != null;
                        addKeywordParameters(keyword, result, parameters.getPosition());
                    }
                }
            }
        });
    }

    private static boolean isArgument(PsiElement current) {
        if (current == null) {
            return false;
        }
        return current.getParent() instanceof Argument;
    }

    private static boolean isKeywordInvokable(PsiElement current) {
        if (current == null) {
            return false;
        }
        return current.getParent() instanceof KeywordInvokable;
    }

    private static boolean isInKeywordStatement(PsiElement current) {
        if (current == null) {
            return false;
        }

        return PsiTreeUtil.getParentOfType(current, KeywordStatement.class) != null;
    }

    private static boolean isValidCompletion(@NotNull CompletionParameters parameters) {
        int offset = parameters.getOffset();
        Document document = parameters.getEditor().getDocument();
        int lineNumber = document.getLineNumber(offset);
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        String textBeforeOffset = document.getText(new TextRange(lineStartOffset, offset));
        return !textBeforeOffset.startsWith(" ") && !textBeforeOffset.startsWith("\t");
    }

    private static PsiElement getHeading(PsiElement current) {
        if (current == null) {
            return null;
        }
        if (current instanceof Heading) {
            return current;
        }
        return getHeading(current.getParent());
    }

    private static boolean containsSettings(@Nullable PsiElement element) {
        if (element instanceof Heading) {
            return ((Heading) element).isSettings();
        }
        return false;
    }

    private static boolean containsTestCases(@Nullable PsiElement element) {
        if (element instanceof Heading) {
            return ((Heading) element).containsTestCases();
        }
        return false;
    }

    private static boolean containsTasks(@Nullable PsiElement element) {
        if (element instanceof Heading) {
            return ((Heading) element).containsTasks();
        }
        return false;
    }

    private static boolean containsKeywordDefinitions(@Nullable PsiElement element) {
        return element instanceof Heading && ((Heading) element).containsKeywordDefinitions();
    }

    private static void addBuiltinLibraryCompletions(CompletionResultSet resultSet, PsiFile file) {
        if (!(file instanceof RobotFile)) {
            return;
        }
        try {
            Map<String, ?> cachedFiles = RobotFileManager.getCachedFiles(file.getProject());
            for (String libraryName : cachedFiles.keySet()) {
                String[] lookupStrings = { libraryName, WordUtils.capitalize(libraryName), libraryName.toLowerCase() };
                LookupElementBuilder elementBuilder = LookupElementBuilder.create(libraryName)
                                                                          .withPresentableText(libraryName)
                                                                          .withLookupStrings(Arrays.asList(lookupStrings))
                                                                          .withCaseSensitivity(true)
                                                                          .withIcon(AllIcons.Nodes.Package)
                                                                          .withTypeText("robot.libraries.Builtin");
                resultSet.addElement(TailTypeDecorator.withTail(elementBuilder, TailTypes.noneType()));
            }
        } catch (Throwable ignored) {
        }
    }

    private static void addResourceFilePaths(CompletionResultSet resultSet, PsiFile file) {
        if (file instanceof RobotFile robotFile) {
            try {
                String basePath = robotFile.getProject().getBasePath();
                if (basePath != null) {
                    for (String filePath : collectFilePaths(new File(basePath))) {
                        if (filePath.endsWith(".resource")) {
                            String relativePath = FileUtil.getRelativePath(new File(robotFile.getContainingDirectory().getVirtualFile().getPath()),
                                                                           new File(filePath));
                            if (relativePath != null) {
                                if (SystemUtils.IS_OS_WINDOWS) {
                                    relativePath = relativePath.replace("\\", "/");
                                }

                                String[] lookupStrings = { relativePath, WordUtils.capitalize(relativePath), relativePath.toLowerCase() };
                                LookupElementBuilder elementBuilder = LookupElementBuilder.create(relativePath)
                                                                                          .withPresentableText(relativePath)
                                                                                          .withLookupStrings(Arrays.asList(lookupStrings))
                                                                                          .withCaseSensitivity(true)
                                                                                          .withIcon(RobotIcons.RESOURCE);
                                resultSet.addElement(TailTypeDecorator.withTail(elementBuilder, TailTypes.noneType()));
                            }
                        }
                    }
                }
            } catch (Throwable ignored) {
            }
        }
    }

    private static List<String> collectFilePaths(File directory) {
        List<String> filePaths = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    filePaths.addAll(collectFilePaths(file));
                } else {
                    filePaths.add(file.getPath());
                }
            }
        }
        return filePaths;
    }

    private static void addDefinedKeywordsFromFile(CompletionResultSet resultSet, PsiFile file) {
        if (file instanceof RobotFile robotFile) {
            boolean capitalizeKeywords = RobotOptionsProvider.getInstance(robotFile.getProject()).capitalizeKeywords();
            addDefinedKeywords(robotFile.getDefinedKeywords(), resultSet, capitalizeKeywords);
            boolean allowTransitiveImports = RobotOptionsProvider.getInstance(file.getProject()).allowTransitiveImports();
            for (KeywordFile importedFile : robotFile.getImportedFiles(allowTransitiveImports)) {
                if (importedFile.getImportType() != ImportType.VARIABLES) {
                    addDefinedKeywords(importedFile.getDefinedKeywords(), resultSet, capitalizeKeywords);
                }
            }
        }
    }

    private static void addDefinedVariablesFromImportedFiles(@NotNull CompletionResultSet resultSet, @NotNull PsiFile file, @Nullable PsiElement element) {
        if (file instanceof RobotFile robotFile) {
            addDefinedVariables(robotFile.getDefinedVariables(), resultSet, element);
            boolean allowTransitiveImports = RobotOptionsProvider.getInstance(file.getProject()).allowTransitiveImports();
            for (KeywordFile importedFile : robotFile.getImportedFiles(allowTransitiveImports)) {
                if (importedFile.getImportType() == ImportType.VARIABLES || importedFile.getImportType() == ImportType.RESOURCE) {
                    addDefinedVariables(importedFile.getDefinedVariables(), resultSet, element);
                }
            }
        }
    }

    private static void addDefinedVariablesFromKeyword(@NotNull CompletionResultSet resultSet, @Nullable PsiElement element) {
        KeywordDefinition keywordDefinition = null;
        if (element != null) {
            for (PsiElement parent = element.getParent(); parent != null; parent = parent.getParent()) {
                if (parent instanceof KeywordDefinition) {
                    keywordDefinition = (KeywordDefinition) parent;
                    break;
                }
            }
        }

        if (keywordDefinition != null) {
            for (DefinedVariable variable : keywordDefinition.getDeclaredVariables()) {
                String lookup = variable.getLookup();
                if (lookup != null) {
                    String[] lookupStrings = { lookup, WordUtils.capitalize(lookup), lookup.toLowerCase() };
                    LookupElementBuilder builder = LookupElementBuilder.create(lookup).withLookupStrings(Arrays.asList(lookupStrings)).withIcon(Nodes.Variable);
                    TailTypeDecorator<LookupElementBuilder> decoratedBuilder = TailTypeDecorator.withTail(addReferenceType(variable.reference(), builder),
                                                                                                          TailTypes.noneType());
                    resultSet.addElement(decoratedBuilder);
                }
            }
        }
    }

    private static void addDefinedVariables(@NotNull Collection<DefinedVariable> variables,
                                            @NotNull CompletionResultSet resultSet,
                                            @Nullable PsiElement element) {
        addDefinedVariables(variables, resultSet, element, TailTypes.noneType());
    }

    private static void addDefinedVariables(@NotNull Collection<DefinedVariable> variables,
                                            @NotNull CompletionResultSet resultSet,
                                            @Nullable PsiElement element,
                                            @NotNull TailType tailType) {
        for (DefinedVariable variable : variables) {
            if (variable.isInScope(element)) {
                String lookup = variable.getLookup();
                if (lookup != null) {
                    String[] lookupStrings = { lookup, WordUtils.capitalize(lookup), lookup.toLowerCase() };
                    LookupElementBuilder builder = LookupElementBuilder.create(lookup).withLookupStrings(Arrays.asList(lookupStrings)).withIcon(Nodes.Variable);
                    TailTypeDecorator<LookupElementBuilder> decoratedBuilder = TailTypeDecorator.withTail(addReferenceType(variable.reference(), builder),
                                                                                                          tailType);
                    resultSet.addElement(decoratedBuilder);
                }
            }
        }
    }

    private static void addKeywordParameters(@NotNull KeywordStatement keywordStatement, @NotNull CompletionResultSet resultSet, @NotNull PsiElement position) {
        Collection<DefinedVariable> availableParameters = keywordStatement.getAvailableParameters();
        Set<String> arguments = keywordStatement.getArguments().stream().map(RobotStatement::getPresentableText).map(text -> {
            int index = text.indexOf('=');
            return index > -1 ? text.substring(0, index) : null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
        availableParameters.removeIf(variable -> arguments.contains(variable.getLookup()));
        addDefinedVariables(availableParameters, resultSet, position, TailType.createSimpleTailType('='));
    }

    public static LookupElementBuilder addReferenceType(PsiElement element, LookupElementBuilder builder) {
        try {
            String fileName = element.getContainingFile().getVirtualFile().getName();
            if (fileName.endsWith(".resource")) {
                builder = builder.withTypeText(getBaseName(fileName), RobotIcons.RESOURCE, true);
            } else if (fileName.endsWith(".robot")) {
                builder = builder.withTypeText(getBaseName(fileName), RobotIcons.FILE, true);
            } else if (fileName.endsWith(".py")) {
                builder = builder.withTypeText(getBaseName(fileName), RobotIcons.PYTHON, true);
            } else if (fileName.endsWith(".pyi")) {
                String directoryName = element.getContainingFile().getContainingDirectory().getName();
                builder = builder.withTypeText(directoryName, RobotIcons.PYTHON, true);
            } else {
                builder = builder.withTypeText(getBaseName(fileName), true);
            }
        } catch (Throwable ignored) {
            // Log or handle the exception if necessary
        }

        return builder;
    }

    private static String getBaseName(String fileName) {
        if (fileName == null) {
            return null;
        }
        int lastIndex = fileName.lastIndexOf('.');
        return (lastIndex == -1) ? fileName : fileName.substring(0, lastIndex);
    }

    private static void addDefinedKeywords(Collection<DefinedKeyword> keywords, CompletionResultSet resultSet, boolean capitalize) {
        for (DefinedKeyword keyword : keywords) {
            String keywordName = keyword.getKeywordName();
            String displayName = capitalize ? WordUtils.capitalize(keywordName) : keywordName;
            String[] lookupStrings = new String[] { keywordName, WordUtils.capitalize(keywordName), keywordName.toLowerCase() };
            LookupElementBuilder lookupElement = LookupElementBuilder.create(displayName)
                                                                     .withLookupStrings(Arrays.asList(lookupStrings))
                                                                     .withPresentableText(displayName)
                                                                     .withCaseSensitivity(true)
                                                                     .withIcon(Nodes.Function);
            LookupElementBuilder decoratedElement = addReferenceType(keyword.reference(), lookupElement);
            displayName = getKeywordArguments(keyword);
            if (displayName != null) {
                decoratedElement = decoratedElement.withTailText(displayName);
            }

            TailTypeDecorator<LookupElementBuilder> tailTypeDecorator = TailTypeDecorator.withTail(decoratedElement,
                                                                                                   keyword.hasArguments() ?
                                                                                                   RobotTailTypes.TAB :
                                                                                                   TailTypes.noneType());
            resultSet.addElement(tailTypeDecorator);
        }
    }

    @Nullable
    public static String getKeywordArguments(DefinedKeyword keyword) {
        if (keyword == null || !keyword.hasArguments()) {
            return null;
        }

        try {
            if (keyword instanceof KeywordDto) {
                return formatArguments(((KeywordDto) keyword).getParameters());
            }

            if (keyword instanceof KeywordDefinitionImpl) {
                return formatArguments(((KeywordDefinitionImpl) keyword).getDefinedArguments());
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private static String formatArguments(Collection<?> arguments) {
        List<String> argumentNames = new ArrayList<>();
        for (Object argument : arguments) {
            if (argument instanceof PyParameter) {
                String name = ((PyParameter) argument).getName();
                if (!((PyParameter) argument).isSelf()) {
                    argumentNames.add(name);
                }
            } else if (argument instanceof DefinedVariable) {
                argumentNames.add(((DefinedVariable) argument).getLookup());
            }
        }
        return " (" + String.join(", ", argumentNames) + ")";
    }

    private static void addSyntaxLookup(@NotNull RobotElementType elementType, @NotNull CompletionResultSet resultSet) {
        List<LookupElement> lookupElements = addSyntaxLookup(elementType);
        resultSet.addAllElements(lookupElements);
    }

    private static List<LookupElement> addSyntaxLookup(@NotNull RobotElementType type) {
        List<LookupElement> results = new ArrayList<>();
        Collection<RecommendationWord> words = RobotKeywordProvider.getRecommendationsForType(type);
        for (RecommendationWord word : words) {
            String text = word.getLookup();
            String lookupString = word.getPresentation();
            String[] lookupStrings = { text, WordUtils.capitalize(text), lookupString, WordUtils.capitalize(lookupString), lookupString.toLowerCase() };
            LookupElement element = TailTypeDecorator.withTail(LookupElementBuilder.create(lookupString)
                                                                                   .withLookupStrings(Arrays.asList(lookupStrings))
                                                                                   .withPresentableText(lookupString)
                                                                                   .withCaseSensitivity(true), word.getTailType());
            results.add(element);
        }
        return results;
    }

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        // debugging point
        super.fillCompletionVariants(parameters, result);
    }
}
