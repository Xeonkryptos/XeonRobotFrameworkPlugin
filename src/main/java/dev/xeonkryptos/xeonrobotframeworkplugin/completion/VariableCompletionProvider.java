package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

import com.intellij.codeInsight.TailTypes;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.LookupElementMarker;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalArgumentsSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotQualifiedNameOwner;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateArguments;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariablesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotFileManager;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.KeywordCallNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.index.VariableDefinitionNameIndex;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RecursiveRobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

class VariableCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement psiElement = parameters.getPosition();
        PsiElement localSettingElement = PsiTreeUtil.getParentOfType(psiElement, RobotLocalSetting.class, RobotLocalArgumentsSetting.class);
        if (localSettingElement instanceof RobotLocalArgumentsSetting) {
            return;
        }

        RobotKeywordCall keywordCall = null;
        RobotElement parentOfInterest = PsiTreeUtil.getParentOfType(psiElement,
                // Stop-gaps to ignore any of the real-interested parents
                                                                    RobotParameter.class, RobotTemplateParameter.class,
                // Really interested in, but only when not one of the previous defined types are matching
                                                                    RobotKeywordCall.class, RobotTemplateArguments.class);
        if (parentOfInterest instanceof RobotTemplateArguments templateArguments) {
            keywordCall = KeywordUtil.findTemplateKeywordCall(templateArguments);
        } else if (parentOfInterest instanceof RobotKeywordCall call) {
            keywordCall = call;
        }
        if (keywordCall != null) {
            OptionalInt startOfKeywordsOnlyIndex = keywordCall.getStartOfKeywordsOnlyIndex();
            if (startOfKeywordsOnlyIndex.isPresent()) {
                int keywordsOnlyStartIndex = startOfKeywordsOnlyIndex.getAsInt();
                if (keywordsOnlyStartIndex == 0) {
                    // As easy as that. With keywords only starting at 0 means, no variables are allowed when a parameter is expected
                    return;
                }

                RobotKeywordCallLocationIdentifier locationIdentifier = new RobotKeywordCallLocationIdentifier(psiElement);
                parentOfInterest.acceptChildren(locationIdentifier);

                int elementIndex = locationIdentifier.getElementIndex();
                if (elementIndex >= keywordsOnlyStartIndex) {
                    // After or at the position where keywords only are expected, no variables are allowed
                    return;
                }
            }
        }

        int completionOffset = parameters.getOffset();
        IElementType elementType = psiElement.getNode().getElementType();
        boolean wrapVariableNames = elementType != RobotTypes.VARIABLE_BODY;

        addGlobalVariables(result, psiElement, wrapVariableNames);
        addDefinedVariablesFromOneself(result, psiElement, completionOffset, wrapVariableNames);
        addArgumentsFromUserKeyword(result, psiElement, wrapVariableNames);
        addGlobalVariablesFromPreviousUserKeywordCalls(result, psiElement, completionOffset, wrapVariableNames);
        addDefinedVariablesFromImportedFiles(result, parameters.getOriginalFile(), psiElement, wrapVariableNames);
    }

    private void addGlobalVariables(@NotNull CompletionResultSet result, @NotNull PsiElement element, boolean wrapVariableNames) {
        Project project = element.getProject();
        Collection<DefinedVariable> globalVariables = RobotFileManager.getGlobalVariables(project);
        Collection<LookupElement> lookupElements = wrapDefinedVariables(globalVariables, element, wrapVariableNames);
        lookupElements.forEach(lookupElement -> lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE));
        result.addAllElements(lookupElements);
    }

    private void addDefinedVariablesFromOneself(@NotNull CompletionResultSet result, @NotNull PsiElement element, int completionOffset, boolean wrapVariableNames) {
        Project project = element.getProject();
        GlobalSearchScope fileScope = GlobalSearchScope.fileScope(element.getContainingFile().getOriginalFile());

        RobotQualifiedNameOwner relevantParent = PsiTreeUtil.getParentOfType(element, RobotTestCaseStatement.class, RobotTaskStatement.class, RobotUserKeywordStatement.class);
        RobotVariableStatement variableStatementParent = PsiTreeUtil.getParentOfType(element, RobotVariableStatement.class);
        if (variableStatementParent != null) {
            completionOffset = variableStatementParent.getTextRange().getStartOffset();
        }
        int finalizedCompletionOffset = completionOffset;
        Collection<DefinedVariable> definedVariables = VariableDefinitionNameIndex.getInstance()
                                                                                  .getVariableDefinitions(project, fileScope)
                                                                                  .stream()
                                                                                  .filter(variableDefinition -> isRelevantVariableDefinitionForOneself(variableDefinition,
                                                                                                                                                       relevantParent,
                                                                                                                                                       finalizedCompletionOffset))
                                                                                  .map(variableDefinition -> (DefinedVariable) variableDefinition)
                                                                                  .collect(Collectors.toCollection(LinkedHashSet::new));

        Collection<LookupElement> lookupElements = wrapDefinedVariables(definedVariables, element, wrapVariableNames);
        lookupElements.forEach(lookupElement -> lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE));
        result.addAllElements(lookupElements);
    }

    private boolean isRelevantVariableDefinitionForOneself(RobotVariableDefinition variableDefinition, PsiElement obligatoryStatementParent, int completionOffset) {
        PsiElement parent = PsiTreeUtil.<PsiElement>getParentOfType(variableDefinition,
                                                                    RobotTestCaseStatement.class,
                                                                    RobotTaskStatement.class,
                                                                    RobotUserKeywordStatement.class,
                                                                    RobotVariablesSection.class);
        if (obligatoryStatementParent != null && parent != null && obligatoryStatementParent.getTextOffset() == parent.getTextOffset()
            && variableDefinition.getTextRange().getStartOffset() < completionOffset) {
            return true;
        }
        return parent instanceof RobotVariablesSection;
    }

    private void addArgumentsFromUserKeyword(@NotNull CompletionResultSet result, @NotNull PsiElement element, boolean wrapVariableNames) {
        RobotUserKeywordStatement userKeywordStatement = PsiTreeUtil.getParentOfType(element, RobotUserKeywordStatement.class);
        if (userKeywordStatement != null) {
            Collection<DefinedVariable> definedVariables = userKeywordStatement.getInputParameters()
                                                                               .stream()
                                                                               .map(DefinedParameter::reference)
                                                                               .map(psiElement -> PsiTreeUtil.findChildOfType(psiElement, RobotVariableDefinition.class, true))
                                                                               .collect(Collectors.toCollection(LinkedHashSet::new));
            addCollectedVariablesWithinKeyword(result, definedVariables, wrapVariableNames);
        }
    }

    private void addGlobalVariablesFromPreviousUserKeywordCalls(@NotNull CompletionResultSet result, @NotNull PsiElement element, int completionOffset, boolean wrapVariableNames) {
        Project project = element.getProject();
        GlobalSearchScope fileScope = GlobalSearchScope.fileScope(element.getContainingFile().getOriginalFile());
        RobotQualifiedNameOwner relevantParent = PsiTreeUtil.getParentOfType(element, RobotTestCaseStatement.class, RobotTaskStatement.class, RobotUserKeywordStatement.class);
        RobotKeywordCall keywordCallParent = PsiTreeUtil.getParentOfType(element, RobotKeywordCall.class);
        if (keywordCallParent != null) {
            completionOffset = keywordCallParent.getTextRange().getStartOffset();
        }
        int finalizedCompletionOffset = completionOffset;
        int parentOffset = relevantParent != null ? relevantParent.getTextOffset() : -1;
        KeywordCallNameIndex.getInstance()
                            .getKeywordCalls(project, fileScope)
                            .stream()
                            .filter(keywordCall -> isRelevantKeywordCall(keywordCall, finalizedCompletionOffset, parentOffset))
                            .map(keywordCall -> keywordCall.getKeywordCallName().getReference().resolve()).distinct()
                                                                                                          .filter(resolvedElement -> resolvedElement instanceof RobotUserKeywordStatement)
                                                                                                          .map(resolvedElement -> (RobotUserKeywordStatement) resolvedElement)
                                                                                                          .forEach(userKeywordStatement -> {
                                                                                                              Collection<DefinedVariable> definedVariables = userKeywordStatement.getDynamicGlobalVariables();
                                                                                                              addCollectedVariablesWithinKeyword(result, definedVariables, wrapVariableNames);
                                                                                                          });
    }

    private boolean isRelevantKeywordCall(RobotKeywordCall keywordCall, int completionOffset, int parentOffset) {
        if (keywordCall.getTextRange().getStartOffset() < completionOffset) {
            RobotQualifiedNameOwner keywordParent = PsiTreeUtil.getParentOfType(keywordCall, RobotTestCaseStatement.class, RobotTaskStatement.class, RobotUserKeywordStatement.class);
            return keywordParent != null && parentOffset == keywordParent.getTextOffset();
        }
        return false;
    }

    private void addDefinedVariablesFromImportedFiles(@NotNull CompletionResultSet result, @NotNull PsiFile file, @NotNull PsiElement element, boolean wrapVariableNames) {
        Collection<KeywordFile> importedFiles = ((RobotFile) file).collectImportedFiles(true, ImportType.VARIABLES, ImportType.RESOURCE);
        for (KeywordFile importedFile : importedFiles) {
            Collection<DefinedVariable> variablesInImportedFile = importedFile.getLocallyDefinedVariables();

            Collection<LookupElement> wrappedVariablesInImportedFile = wrapDefinedVariables(variablesInImportedFile, element, wrapVariableNames);
            wrappedVariablesInImportedFile.forEach(lookupElement -> lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE));

            result.addAllElements(wrappedVariablesInImportedFile);
        }
    }

    private static void addCollectedVariablesWithinKeyword(@NotNull CompletionResultSet result, Collection<DefinedVariable> definedVariables, boolean wrapVariableNames) {
        List<LookupElement> wrappedVariables = definedVariables.stream()
                                                               .map(variable -> createDecoratedLookupElementMarkerIfNecessary(variable, wrapVariableNames))
                                                               .map(variable -> CompletionProviderUtils.createLookupElement(variable, Nodes.Variable, false, TailTypes.noneType()))
                                                               .filter(Optional::isPresent)
                                                               .map(optional -> {
                                                                   LookupElement lookupElement = optional.get();
                                                                   lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.WITHIN_KEYWORD_STATEMENT);
                                                                   lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE);
                                                                   return lookupElement;
                                                               })
                                                               .toList();
        result.addAllElements(wrappedVariables);
    }

    private Collection<LookupElement> wrapDefinedVariables(@NotNull Collection<DefinedVariable> variables, @NotNull PsiElement element, boolean wrapNamesForInsertion) {
        return variables.stream()
                        .filter(variable -> variable.isInScope(element))
                        .map(variable -> createDecoratedLookupElementMarkerIfNecessary(variable, wrapNamesForInsertion))
                        .map(variable -> CompletionProviderUtils.createLookupElement(variable, Nodes.Variable, false, TailTypes.noneType()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList();
    }

    private static LookupElementMarker createDecoratedLookupElementMarkerIfNecessary(DefinedVariable variable, boolean wrapNamesForInsertion) {
        return wrapNamesForInsertion ? new VariableLookupElementMarkerDelegate(variable) : variable;
    }

    private static final class RobotKeywordCallLocationIdentifier extends RecursiveRobotVisitor {

        private final int startOffsetInParent;

        private int currentIndex = -1;
        private int elementIndex = -1;

        private RobotKeywordCallLocationIdentifier(PsiElement sourceElement) {
            startOffsetInParent = sourceElement.getTextOffset();
        }

        @Override
        public void visitArgument(@NotNull RobotArgument o) {
            ++currentIndex;

            int currentOffsetInParent = o.getTextOffset();
            updateFoundElementIndex(currentOffsetInParent);
        }

        private void updateFoundElementIndex(int currentOffsetInParent) {
            if (elementIndex == -1 && currentOffsetInParent >= startOffsetInParent) {
                elementIndex = currentIndex;
            }
        }

        public int getElementIndex() {
            if (currentIndex != -1 && elementIndex == -1) {
                elementIndex = currentIndex;
            }
            return elementIndex;
        }
    }
}
