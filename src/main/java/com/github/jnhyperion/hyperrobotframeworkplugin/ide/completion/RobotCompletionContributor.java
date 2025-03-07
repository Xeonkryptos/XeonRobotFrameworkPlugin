package com.github.jnhyperion.hyperrobotframeworkplugin.ide.completion;

import com.github.jnhyperion.hyperrobotframeworkplugin.ide.config.RobotOptionsProvider;
import com.github.jnhyperion.hyperrobotframeworkplugin.ide.icons.RobotIcons;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RecommendationWord;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotElementType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotKeywordProvider;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotResourceFileType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotStubTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.RobotTokenTypes;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.ImportType;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto.KeywordDto;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Parameter;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.PositionalArgument;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedKeyword;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedParameter;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.DefinedVariable;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Heading;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.Import;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinition;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordDefinitionImpl;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordFile;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordStatement;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.LookupElementMarker;
import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.ParameterId;
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
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.TailTypeDecorator;
import com.intellij.icons.AllIcons;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import com.jetbrains.python.psi.PyParameter;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RobotCompletionContributor extends CompletionContributor {

    public RobotCompletionContributor() {
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andNot(PlatformPatterns.psiComment())
                               .andNot(withArgumentInKeywordStatement())
                               .andNot(PlatformPatterns.psiElement(RobotTokenTypes.SYNTAX_MARKER))
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new CompletionProvider<>() {
                   @Override
                   protected void addCompletions(@NotNull CompletionParameters parameters,
                                                 @NotNull ProcessingContext context,
                                                 @NotNull CompletionResultSet result) {
                       if (!isIndexPositionAWhitespaceCharacter(parameters)) {
                           boolean isResource = parameters.getOriginalFile().getFileType() instanceof RobotResourceFileType;

                           for (LookupElement element : addSyntaxLookup(RobotTokenTypes.HEADING)) {
                               String lookupString = element.getLookupString();
                               if (!isResource || !"*** Test Cases ***".equals(lookupString) && !"*** Tasks ***".equals(lookupString)) {
                                   result.addElement(element);
                               }
                           }
                       }
                   }
               });
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andNot(PlatformPatterns.psiComment())
                               .andNot(PlatformPatterns.psiElement(RobotTokenTypes.ARGUMENT))
                               .andNot(PlatformPatterns.psiElement(RobotTokenTypes.PARAMETER))
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new CompletionProvider<>() {
                   @Override
                   protected void addCompletions(@NotNull CompletionParameters parameters,
                                                 @NotNull ProcessingContext context,
                                                 @NotNull CompletionResultSet result) {
                       Heading heading = getHeading(parameters.getOriginalPosition());
                       if (isIndexPositionAWhitespaceCharacter(parameters) &&
                           heading != null &&
                           (heading.containsTestCases() || heading.containsKeywordDefinitions())) {
                           RobotCompletionContributor.addSyntaxLookup(RobotTokenTypes.BRACKET_SETTING, result);
                       }
                   }
               });
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andNot(PlatformPatterns.psiComment())
                               .andNot(PlatformPatterns.psiElement(RobotTokenTypes.ARGUMENT))
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new CompletionProvider<>() {
                   @Override
                   protected void addCompletions(@NotNull CompletionParameters parameters,
                                                 @NotNull ProcessingContext context,
                                                 @NotNull CompletionResultSet result) {
                       PsiElement originalPosition = parameters.getOriginalPosition();
                       Heading heading = getHeading(originalPosition);
                       if (heading != null && heading.isSettings()) {
                           addSyntaxLookup(RobotTokenTypes.SETTING, result);
                           addSyntaxLookup(RobotTokenTypes.IMPORT, result);
                       }
                   }
               });
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andNot(PlatformPatterns.psiComment())
                               .and(PlatformPatterns.psiElement(RobotTokenTypes.ARGUMENT)
                                                    .withSuperParent(2, PlatformPatterns.psiElement(RobotTokenTypes.IMPORT)))
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new CompletionProvider<>() {
                   @Override
                   protected void addCompletions(@NotNull CompletionParameters parameters,
                                                 @NotNull ProcessingContext context,
                                                 @NotNull CompletionResultSet result) {
                       Import importElement = PsiTreeUtil.getParentOfType(parameters.getPosition(), Import.class);
                       if (importElement != null && importElement.isLibrary()) {
                           addBuiltinLibraryCompletions(result, parameters.getOriginalFile());
                           if (importElement.getChildren().length > 1) {
                               for (LookupElement lookupElement : addSyntaxLookup(RobotTokenTypes.SYNTAX_MARKER)) {
                                   if ("AS".equals(lookupElement.getLookupString())) {
                                       result.addElement(lookupElement);
                                   }
                               }
                           }
                       }

                       if (importElement != null && importElement.isResource()) {
                           addResourceFilePaths(result, parameters.getOriginalFile());
                       }
                   }
               });
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andNot(PlatformPatterns.psiComment())
                               .andNot(PlatformPatterns.psiElement(RobotTokenTypes.ARGUMENT))
                               .andNot(PlatformPatterns.psiElement(RobotTokenTypes.PARAMETER))
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new CompletionProvider<>() {
                   @Override
                   protected void addCompletions(@NotNull CompletionParameters parameters,
                                                 @NotNull ProcessingContext context,
                                                 @NotNull CompletionResultSet result) {
                       Heading heading = getHeading(parameters.getOriginalPosition());
                       if (isIndexPositionAWhitespaceCharacter(parameters) && heading != null && heading.containsTestCases()) {
                           addSyntaxLookup(RobotTokenTypes.GHERKIN, result);
                       }
                   }
               });
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andNot(PlatformPatterns.psiComment())
                               .andNot(withArgumentInKeywordStatement())
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new CompletionProvider<>() {
                   @Override
                   protected void addCompletions(@NotNull CompletionParameters parameters,
                                                 @NotNull ProcessingContext context,
                                                 @NotNull CompletionResultSet result) {
                       Heading heading = getHeading(parameters.getOriginalPosition());
                       if (isIndexPositionAWhitespaceCharacter(parameters) &&
                           heading != null &&
                           (heading.containsTestCases() || heading.containsKeywordDefinitions())) {
                           List<LookupElement> lookupElements = addSyntaxLookup(RobotTokenTypes.SYNTAX_MARKER);
                           List<LookupElement> nonSpecialElements = new ArrayList<>();
                           List<LookupElement> specialElements = new ArrayList<>();

                           for (LookupElement element : lookupElements) {
                               String lookupString = element.getLookupString();
                               if (!"AS".equals(lookupString) && !lookupString.startsWith("IN")) {
                                   nonSpecialElements.add(element);
                               } else {
                                   specialElements.add(element);
                               }
                           }

                           if (!isArgument(parameters.getPosition())) {
                               result.addAllElements(nonSpecialElements);
                           } else {
                               result.addAllElements(specialElements);
                           }
                       }
                   }
               });
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andNot(PlatformPatterns.psiComment())
                               .andNot(withArgumentInKeywordStatement())
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new CompletionProvider<>() {
                   @Override
                   protected void addCompletions(@NotNull CompletionParameters parameters,
                                                 @NotNull ProcessingContext context,
                                                 @NotNull CompletionResultSet result) {
                       Heading heading = getHeading(parameters.getPosition());
                       if (isIndexPositionAWhitespaceCharacter(parameters) &&
                           heading != null &&
                           (heading.containsTestCases() || heading.containsKeywordDefinitions() || heading.containsTasks())) {
                           boolean startingWithSlash = isStartingWithSlash(parameters);
                           addDefinedKeywordsFromFile(result, parameters.getOriginalFile(), startingWithSlash);
                       } else if (heading != null && heading.isSettings()) {
                           addDefinedKeywordsFromFile(result, parameters.getOriginalFile(), false);
                       }
                   }
               });
        // Provide parameter completions in context of keyword statements
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andNot(PlatformPatterns.psiComment())
                               .and(withArgumentInKeywordStatement())
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new CompletionProvider<>() {
                   @Override
                   protected void addCompletions(@NotNull CompletionParameters parameters,
                                                 @NotNull ProcessingContext context,
                                                 @NotNull CompletionResultSet result) {
                       KeywordStatement keyword = PsiTreeUtil.getParentOfType(parameters.getPosition(), KeywordStatement.class);
                       if (keyword != null) {
                           PsiElement psiParent = parameters.getPosition().getParent();
                           PsiElement superParent = psiParent.getParent();
                           if (superParent instanceof Parameter) {
                               if ("=".equals(psiParent.getPrevSibling().getText())) {
                                   return;
                               }
                               result = result.withPrefixMatcher("");
                           }
                           addKeywordParameters(keyword, result);
                       }
                   }
               });
        // Provide completions in context of variables or arguments
        extend(CompletionType.BASIC,
               PlatformPatterns.psiElement()
                               .andOr(PlatformPatterns.psiElement(RobotTokenTypes.VARIABLE), PlatformPatterns.psiElement(RobotTokenTypes.ARGUMENT))
                               .inFile(PlatformPatterns.psiElement(RobotFile.class)),
               new CompletionProvider<>() {
                   @Override
                   protected void addCompletions(@NotNull CompletionParameters parameters,
                                                 @NotNull ProcessingContext context,
                                                 @NotNull CompletionResultSet result) {
                       PsiElement psiElement = parameters.getPosition();
                       Parameter parameter = PsiTreeUtil.getParentOfType(psiElement, Parameter.class);
                       if (parameter != null) {
                           // In parameter context, the prefix usually contains the parameter name, too. For finding and filtering variable names, we need to
                           // remove the parameter name from the prefix.
                           String prefix = result.getPrefixMatcher().getPrefix();
                           String newPrefix;
                           if (prefix.startsWith("=")) {
                               newPrefix = prefix.substring(1);
                           } else if (prefix.startsWith(parameter.getParameterName() + "=")) {
                               int parameterDefinitionLength = (parameter.getParameterName() + "=").length();
                               newPrefix = prefix.substring(parameterDefinitionLength);
                           } else {
                               newPrefix = prefix;
                           }
                           result = result.withPrefixMatcher(newPrefix);
                       }

                       addDefinedVariablesFromImportedFiles(result, parameters.getOriginalFile(), psiElement);
                       addDefinedVariablesFromKeyword(result, psiElement);
                   }
               });
    }

    private static PsiElementPattern.Capture<PsiElement> withArgumentInKeywordStatement() {
        return PlatformPatterns.psiElement(RobotTokenTypes.ARGUMENT).withAncestor(3, PlatformPatterns.psiElement(RobotStubTokenTypes.KEYWORD_STATEMENT));
    }

    private static boolean isArgument(PsiElement current) {
        if (current == null) {
            return false;
        }
        return current.getParent() instanceof PositionalArgument;
    }

    private static boolean isIndexPositionAWhitespaceCharacter(@NotNull CompletionParameters parameters) {
        int offset = parameters.getOffset();
        Document document = parameters.getEditor().getDocument();
        int lineNumber = document.getLineNumber(offset);
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        String textBeforeOffset = document.getText(new TextRange(lineStartOffset, offset));
        if (textBeforeOffset.isEmpty()) {
            return false;
        }
        int firstCharacterInLine = textBeforeOffset.codePointAt(0);
        return Character.isWhitespace(firstCharacterInLine);
    }

    private static boolean isStartingWithSlash(@NotNull CompletionParameters parameters) {
        int offset = parameters.getOffset();

        Document document = parameters.getEditor().getDocument();
        int lineNumber = document.getLineNumber(offset);
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        String textBeforeOffset = document.getText(new TextRange(lineStartOffset, offset));
        if (textBeforeOffset.isEmpty()) {
            return false;
        }
        int firstCharacterInLine = textBeforeOffset.trim().codePointAt(0);
        return "/".equals(Character.toString(firstCharacterInLine));
    }

    private static Heading getHeading(PsiElement current) {
        if (current == null) {
            return null;
        }
        if (current instanceof Heading heading) {
            return heading;
        }
        return PsiTreeUtil.getParentOfType(current, Heading.class);
    }

    private static void addBuiltinLibraryCompletions(CompletionResultSet resultSet, PsiFile file) {
        if (!(file instanceof RobotFile)) {
            return;
        }
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
    }

    private static void addResourceFilePaths(CompletionResultSet resultSet, PsiFile file) {
        if (file instanceof RobotFile robotFile) {
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

    private static void addDefinedKeywordsFromFile(CompletionResultSet resultSet, PsiFile file, boolean addKeywordParametersOnInsert) {
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

    private static void addDefinedVariablesFromImportedFiles(@NotNull CompletionResultSet resultSet, @NotNull PsiFile file, @Nullable PsiElement element) {
        if (file instanceof RobotFile robotFile) {
            addDefinedVariables(robotFile.getDefinedVariables(), resultSet, element).forEach(lookupElement -> {
                lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.WITHIN_KEYWORD_STATEMENT);
                lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE);
            });
            boolean allowTransitiveImports = RobotOptionsProvider.getInstance(file.getProject()).allowTransitiveImports();
            for (KeywordFile importedFile : robotFile.getImportedFiles(allowTransitiveImports)) {
                if (importedFile.getImportType() == ImportType.VARIABLES || importedFile.getImportType() == ImportType.RESOURCE) {
                    addDefinedVariables(importedFile.getDefinedVariables(), resultSet, element).forEach(lookupElement -> {
                        lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.WITHIN_KEYWORD_STATEMENT);
                        lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE);
                    });
                }
            }
        }
    }

    private static void addDefinedVariablesFromKeyword(@NotNull CompletionResultSet resultSet, @NotNull PsiElement element) {
        KeywordDefinition keywordDefinition = PsiTreeUtil.getParentOfType(element, KeywordDefinition.class);
        if (keywordDefinition != null) {
            for (DefinedVariable variable : keywordDefinition.getDeclaredVariables()) {
                addLookupElement(variable, Nodes.Variable, false, TailTypes.noneType(), resultSet).ifPresent(lookupElement -> {
                    lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.WITHIN_KEYWORD_STATEMENT);
                    lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE);
                });
            }
        }
    }

    private static Collection<LookupElement> addDefinedVariables(@NotNull Collection<DefinedVariable> variables,
                                                                 @NotNull CompletionResultSet resultSet,
                                                                 @Nullable PsiElement element) {
        return addDefinedVariables(variables, resultSet, element, TailTypes.noneType());
    }

    private static Collection<LookupElement> addDefinedVariables(@NotNull Collection<DefinedVariable> variables,
                                                                 @NotNull CompletionResultSet resultSet,
                                                                 @Nullable PsiElement element,
                                                                 @NotNull TailType tailType) {
        return variables.stream()
                        .filter(variable -> variable.isInScope(element))
                        .map(variable -> addLookupElement(variable, Nodes.Variable, false, tailType, resultSet))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList();
    }

    private static void addKeywordParameters(@NotNull KeywordStatement keywordStatement, @NotNull CompletionResultSet resultSet) {
        Collection<DefinedParameter> availableParameters = keywordStatement.getAvailableParameters();
        Set<String> arguments = keywordStatement.getParameters()
                                                .stream()
                                                .flatMap(parameter -> PsiTreeUtil.getChildrenOfTypeAsList(parameter, ParameterId.class).stream())
                                                .map(RobotStatement::getPresentableText)
                                                .collect(Collectors.toSet());
        availableParameters.removeIf(variable -> arguments.contains(variable.getLookup()));

        TailType assignmentTailType = TailType.createSimpleTailType('=');
        for (DefinedParameter parameter : availableParameters) {
            addLookupElement(parameter, Nodes.Parameter, true, assignmentTailType, resultSet).ifPresent(lookupElement -> {
                lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.WITHIN_KEYWORD_STATEMENT);
                lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.PARAMETER);
            });
        }
    }

    private static Optional<LookupElement> addLookupElement(LookupElementMarker lookupElementMarker,
                                                            @Nullable Icon icon,
                                                            boolean bold,
                                                            @NotNull TailType tailType,
                                                            @NotNull CompletionResultSet resultSet) {
        String lookup = lookupElementMarker.getLookup();
        if (lookup != null) {
            List<String> lookupStrings = List.of(lookup, WordUtils.capitalize(lookup), lookup.toLowerCase());
            LookupElementBuilder builder = LookupElementBuilder.create(lookup)
                                                               .withLookupStrings(lookupStrings)
                                                               .withIcon(icon)
                                                               .withPsiElement(lookupElementMarker.reference())
                                                               .withInsertHandler((context, item) -> {
                                                                   if (item.getUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE) ==
                                                                       RobotLookupElementType.VARIABLE) {
                                                                       String lookupString = item.getLookupString();

                                                                       Editor editor = context.getEditor();
                                                                       Document document = context.getDocument();
                                                                       int startOffset = context.getStartOffset();
                                                                       int selectionEndOffset = context.getSelectionEndOffset();
                                                                       String text = document.getText(new TextRange(selectionEndOffset,
                                                                                                                    selectionEndOffset + 1));
                                                                       if (text.endsWith("}")) {
                                                                           selectionEndOffset += 1;
                                                                       }
                                                                       int newEndOffset = startOffset + lookupString.length();
                                                                       document.replaceString(startOffset, selectionEndOffset, lookupString);
                                                                       editor.getCaretModel().moveToOffset(newEndOffset);
                                                                       context.setTailOffset(newEndOffset);
                                                                   }
                                                               });
            if (bold) {
                builder = builder.bold();
            }
            builder = addReferenceType(lookupElementMarker.reference(), builder);
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
            resultSet.addElement(lookupElement);
            return Optional.of(lookupElement);
        }
        return Optional.empty();
    }

    public static LookupElementBuilder addReferenceType(PsiElement element, LookupElementBuilder builder) {
        VirtualFile virtualFile = element.getContainingFile().getVirtualFile();
        if (virtualFile != null) {
            String fileName = virtualFile.getName();
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

    private static Collection<LookupElement> addDefinedKeywords(Collection<DefinedKeyword> keywords,
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
            LookupElementBuilder decoratedElement = addReferenceType(keyword.reference(), lookupElement);
            displayName = getKeywordArguments(keyword);
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

    @Nullable
    public static String getKeywordArguments(DefinedKeyword keyword) {
        if (keyword == null || !keyword.hasParameters()) {
            return null;
        }

        try {
            if (keyword instanceof KeywordDto) {
                return formatArguments(keyword.getParameters());
            }

            if (keyword instanceof KeywordDefinitionImpl keywordDefinition) {
                return formatArguments(keywordDefinition.getDefinedVariables());
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
            String text = word.lookup();
            String lookupString = word.presentation();
            String[] lookupStrings = { text, WordUtils.capitalize(text), lookupString, WordUtils.capitalize(lookupString), lookupString.toLowerCase() };
            LookupElement element = TailTypeDecorator.withTail(LookupElementBuilder.create(lookupString)
                                                                                   .withLookupStrings(Arrays.asList(lookupStrings))
                                                                                   .withPresentableText(lookupString)
                                                                                   .withCaseSensitivity(true), word.tailType());
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
