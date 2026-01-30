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
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateArguments;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTemplateParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.RobotFileManager;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RecursiveRobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotInStatementVariableCollector;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotSectionVariablesCollector;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

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

        IElementType elementType = psiElement.getNode().getElementType();
        boolean wrapVariableNames = elementType != RobotTypes.VARIABLE_BODY;

        addGlobalVariables(result, psiElement, wrapVariableNames);
        addDefinedVariablesFromOwnSection(result, psiElement, wrapVariableNames);
        addArgumentsFromUserKeyword(result, psiElement, wrapVariableNames);
        addDefinedVariablesFromImportedFiles(result, parameters.getOriginalFile(), psiElement, wrapVariableNames);
    }

    private void addGlobalVariables(@NotNull CompletionResultSet result, @NotNull PsiElement element, boolean wrapVariableNames) {
        Project project = element.getProject();
        Collection<DefinedVariable> globalVariables = RobotFileManager.getGlobalVariables(project);
        Collection<LookupElement> lookupElements = wrapDefinedVariables(globalVariables, element, wrapVariableNames);
        lookupElements.forEach(lookupElement -> lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE));
        result.addAllElements(lookupElements);
    }

    private void addDefinedVariablesFromOwnSection(@NotNull CompletionResultSet result, @NotNull PsiElement element, boolean wrapVariableNames) {
        PsiElement robotStatement = PsiTreeUtil.getParentOfType(element, RobotTestCaseStatement.class, RobotTaskStatement.class, RobotUserKeywordStatement.class);
        if (robotStatement == null) {
            return;
        }

        RobotInStatementVariableCollector variableCollector = new RobotInStatementVariableCollector(element);
        robotStatement.accept(variableCollector);

        Collection<DefinedVariable> definedVariables = variableCollector.getAvailableVariables();
        Collection<LookupElement> lookupElements = wrapDefinedVariables(definedVariables, element, wrapVariableNames);
        lookupElements.forEach(lookupElement -> lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE));
        result.addAllElements(lookupElements);
    }

    private void addDefinedVariablesFromImportedFiles(@NotNull CompletionResultSet result, @NotNull PsiFile file, @NotNull PsiElement element, boolean wrapVariableNames) {
        RobotFile robotFile = (RobotFile) file;
        Collection<DefinedVariable> variablesInCurrentFile = robotFile.getLocallyDefinedVariables();

        Collection<LookupElement> wrappedVariablesInCurrentFile = wrapDefinedVariables(variablesInCurrentFile, element, wrapVariableNames);
        wrappedVariablesInCurrentFile.forEach(lookupElement -> lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE));

        result.addAllElements(wrappedVariablesInCurrentFile);

        Collection<KeywordFile> importedFiles = robotFile.collectImportedFiles(true, ImportType.VARIABLES, ImportType.RESOURCE);
        for (KeywordFile importedFile : importedFiles) {
            Collection<DefinedVariable> variablesInImportedFile = importedFile.getLocallyDefinedVariables();

            Collection<LookupElement> wrappedVariablesInImportedFile = wrapDefinedVariables(variablesInImportedFile, element, wrapVariableNames);
            wrappedVariablesInImportedFile.forEach(lookupElement -> lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE));

            result.addAllElements(wrappedVariablesInImportedFile);
        }
    }

    private void addArgumentsFromUserKeyword(@NotNull CompletionResultSet result, @NotNull PsiElement element, boolean wrapVariableNames) {
        RobotUserKeywordStatement userKeywordStatement = PsiTreeUtil.getParentOfType(element, RobotUserKeywordStatement.class);
        if (userKeywordStatement != null) {
            List<RobotLocalArgumentsSetting> localArgumentsSettingList = userKeywordStatement.getLocalArgumentsSettingList();
            if (!localArgumentsSettingList.isEmpty()) {
                RobotSectionVariablesCollector variablesCollector = new RobotSectionVariablesCollector();
                RobotLocalArgumentsSetting localArgumentsSetting = localArgumentsSettingList.getFirst();
                localArgumentsSetting.acceptChildren(variablesCollector);

                Collection<DefinedVariable> definedVariables = variablesCollector.getVariables();
                addCollectedVariablesWithinKeyword(result, definedVariables, wrapVariableNames);

                definedVariables = variablesCollector.computeUserKeywordVariables();
                addCollectedVariablesWithinKeyword(result, definedVariables, wrapVariableNames);
            }
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
