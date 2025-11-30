package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.TailTypes;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.icons.AllIcons.Nodes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
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
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.KeywordUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RecursiveRobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotInStatementVariableCollector;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotSectionVariablesCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;

class VariableCompletionProvider extends CompletionProvider<CompletionParameters> {

    private static final Set<String> RESTRICTED_VARIABLE_COMPLETION_LOCAL_SETTING_NAMES = Set.of("[Documentation]", "[Tags]");

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement psiElement = parameters.getPosition();
        PsiElement localSettingElement = PsiTreeUtil.getParentOfType(psiElement, RobotLocalSetting.class, RobotLocalArgumentsSetting.class);
        if (localSettingElement instanceof RobotLocalArgumentsSetting || localSettingElement != null
                                                                         && RESTRICTED_VARIABLE_COMPLETION_LOCAL_SETTING_NAMES.contains(((RobotLocalSetting) localSettingElement).getSettingName())) {
            return;
        }

        Collection<LookupElement> lookupElements = new LinkedList<>();
        RobotKeywordCall keywordCall = null;
        RobotElement parentOfInterest = PsiTreeUtil.getParentOfType(psiElement,
                                                                    // Stop-gaps to ignore any of the real-interested parents
                                                                    RobotParameter.class, RobotTemplateParameter.class,
                                                                    // Really interested in, but only when not one of the previous defined types are matching
                                                                    RobotKeywordCall.class, RobotTemplateArguments.class);
        if (parentOfInterest instanceof RobotTemplateArguments templateArguments) {
            keywordCall = KeywordUtil.getInstance(templateArguments.getProject()).findTemplateKeywordCall(templateArguments);
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

        addDefinedVariablesFromImportedFiles(lookupElements, parameters.getOriginalFile(), psiElement);
        addDefinedVariablesFromOwnSection(lookupElements, psiElement);
        addArgumentsFromUserKeyword(lookupElements, psiElement);

        result.addAllElements(lookupElements);
    }

    private void addDefinedVariablesFromImportedFiles(@NotNull Collection<LookupElement> lookupElements, @NotNull PsiFile file, @NotNull PsiElement element) {
        RobotFile robotFile = (RobotFile) file;
        addDefinedVariables(robotFile.getDefinedVariables(), element).forEach(lookupElement -> {
            lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE);
            lookupElements.add(lookupElement);
        });
        for (KeywordFile importedFile : robotFile.collectImportedFiles(true, ImportType.VARIABLES, ImportType.RESOURCE)) {
            addDefinedVariables(importedFile.getDefinedVariables(), element).forEach(lookupElement -> {
                lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE);
                lookupElements.add(lookupElement);
            });
        }
    }

    private void addDefinedVariablesFromOwnSection(@NotNull Collection<LookupElement> lookupElements, @NotNull PsiElement element) {
        PsiElement robotStatement = PsiTreeUtil.getParentOfType(element,
                                                                RobotTestCaseStatement.class,
                                                                RobotTaskStatement.class,
                                                                RobotUserKeywordStatement.class);
        if (robotStatement == null) {
            return;
        }

        RobotInStatementVariableCollector variableCollector = new RobotInStatementVariableCollector(element);
        robotStatement.accept(variableCollector);
        Collection<DefinedVariable> definedVariables = variableCollector.getAvailableVariables();
        if (!definedVariables.isEmpty()) {
            addDefinedVariables(definedVariables, element).forEach(lookupElement -> {
                lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE);
                lookupElements.add(lookupElement);
            });
        }
    }

    private void addArgumentsFromUserKeyword(@NotNull Collection<LookupElement> lookupElements, @NotNull PsiElement element) {
        RobotUserKeywordStatement userKeywordStatement = PsiTreeUtil.getParentOfType(element, RobotUserKeywordStatement.class);
        if (userKeywordStatement != null) {
            List<RobotLocalArgumentsSetting> localArgumentsSettingList = userKeywordStatement.getLocalArgumentsSettingList();
            if (!localArgumentsSettingList.isEmpty()) {
                RobotSectionVariablesCollector variablesCollector = new RobotSectionVariablesCollector();
                RobotLocalArgumentsSetting localArgumentsSetting = localArgumentsSettingList.getFirst();
                localArgumentsSetting.acceptChildren(variablesCollector);
                Collection<DefinedVariable> definedVariables = variablesCollector.getVariables();
                for (DefinedVariable variable : definedVariables) {
                    CompletionProviderUtils.createLookupElement(variable, Nodes.Variable, false, TailTypes.noneType()).ifPresent(lookupElement -> {
                        lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.WITHIN_KEYWORD_STATEMENT);
                        lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE);
                        lookupElements.add(lookupElement);
                    });
                }
            }
        }
    }

    private Collection<LookupElement> addDefinedVariables(@NotNull Collection<DefinedVariable> variables, @NotNull PsiElement element) {
        return addDefinedVariables(variables, element, TailTypes.noneType());
    }

    private Collection<LookupElement> addDefinedVariables(@NotNull Collection<DefinedVariable> variables,
                                                          @NotNull PsiElement element,
                                                          @NotNull TailType tailType) {
        return variables.stream()
                        .filter(variable -> variable.isInScope(element))
                        .map(variable -> CompletionProviderUtils.createLookupElement(variable, Nodes.Variable, false, tailType))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList();
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
