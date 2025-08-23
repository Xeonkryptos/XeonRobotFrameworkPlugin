package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

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
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotParameter;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTaskStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RecursiveRobotVisitor;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotInStatementVariableCollector;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotSectionVariablesCollector;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

class VariableCompletionProvider extends CompletionProvider<CompletionParameters> {

    private static final Set<String> RESTRICTED_VARIABLE_COMPLETION_LOCAL_SETTING_NAMES = Set.of("[Arguments]", "[Documentation]", "[Tags]");

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        RobotLocalSetting localSettingElement = PsiTreeUtil.getParentOfType(parameters.getOriginalPosition(), RobotLocalSetting.class);
        if (localSettingElement != null && RESTRICTED_VARIABLE_COMPLETION_LOCAL_SETTING_NAMES.contains(localSettingElement.getSettingName())) {
            return;
        }

        PsiElement psiElement = parameters.getPosition();
        RobotStatement parentOfInterest = PsiTreeUtil.getParentOfType(psiElement, RobotParameter.class, RobotKeywordCall.class);
        if (parentOfInterest instanceof RobotKeywordCall keywordCall) {
            Optional<Integer> startOfKeywordsOnlyIndex = keywordCall.getStartOfKeywordsOnlyIndex();
            if (startOfKeywordsOnlyIndex.isPresent()) {
                int keywordsOnlyStartIndex = startOfKeywordsOnlyIndex.get();
                if (keywordsOnlyStartIndex == 0) {
                    // As easy as that. With keywords only starting at 0 means, no variables are allowed when a parameter is expected
                    return;
                }
                RobotKeywordCallLocationIdentifier locationIdentifier = new RobotKeywordCallLocationIdentifier(psiElement);
                keywordCall.acceptChildren(locationIdentifier);

                int elementIndex = locationIdentifier.getElementIndex();
                if (elementIndex >= keywordsOnlyStartIndex) {
                    // After or at the position where keywords only are expected, no variables are allowed
                    return;
                }
            }
        }

        addDefinedVariablesFromImportedFiles(result, parameters.getOriginalFile(), psiElement);
        addDefinedVariablesFromOwnSection(result, psiElement);
        addDefinedVariablesFromKeyword(result, psiElement);
    }

    private void addDefinedVariablesFromImportedFiles(@NotNull CompletionResultSet resultSet, @NotNull PsiFile file, @NotNull PsiElement element) {
        RobotFile robotFile = (RobotFile) file;
        addDefinedVariables(robotFile.getDefinedVariables(),
                            resultSet,
                            element).forEach(lookupElement -> lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE,
                                                                                        RobotLookupElementType.VARIABLE));
        boolean allowTransitiveImports = RobotOptionsProvider.getInstance(file.getProject()).allowTransitiveImports();
        for (KeywordFile importedFile : robotFile.collectImportedFiles(allowTransitiveImports)) {
            if (importedFile.getImportType() == ImportType.VARIABLES || importedFile.getImportType() == ImportType.RESOURCE) {
                addDefinedVariables(importedFile.getDefinedVariables(),
                                    resultSet,
                                    element).forEach(lookupElement -> lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE,
                                                                                                RobotLookupElementType.VARIABLE));
            }
        }
    }

    private void addDefinedVariablesFromOwnSection(@NotNull CompletionResultSet resultSet, @NotNull PsiElement element) {
        PsiElement robotStatement = PsiTreeUtil.getParentOfType(element, RobotTestCaseStatement.class, RobotTaskStatement.class);
        if (robotStatement == null) {
            return;
        }

        RobotInStatementVariableCollector variableCollector = new RobotInStatementVariableCollector(element);
        robotStatement.accept(variableCollector);
        Collection<DefinedVariable> definedVariables = variableCollector.getAvailableVariables();
        if (!definedVariables.isEmpty()) {
            addDefinedVariables(definedVariables,
                                resultSet,
                                element).forEach(lookupElement -> lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE,
                                                                                            RobotLookupElementType.VARIABLE));
        }
    }

    private void addDefinedVariablesFromKeyword(@NotNull CompletionResultSet resultSet, @NotNull PsiElement element) {
        RobotUserKeywordStatement userKeywordStatement = PsiTreeUtil.getParentOfType(element, RobotUserKeywordStatement.class);
        if (userKeywordStatement != null) {
            RobotSectionVariablesCollector variablesCollector = new RobotSectionVariablesCollector();
            userKeywordStatement.accept(variablesCollector);
            Collection<DefinedVariable> definedVariables = variablesCollector.getVariables();
            for (DefinedVariable variable : definedVariables) {
                CompletionProviderUtils.addLookupElement(variable, Nodes.Variable, false, TailTypes.noneType(), resultSet).ifPresent(lookupElement -> {
                    lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_CONTEXT, RobotLookupContext.WITHIN_KEYWORD_STATEMENT);
                    lookupElement.putUserData(CompletionKeys.ROBOT_LOOKUP_ELEMENT_TYPE, RobotLookupElementType.VARIABLE);
                });
            }
        }
    }

    private Collection<LookupElement> addDefinedVariables(@NotNull Collection<DefinedVariable> variables,
                                                          @NotNull CompletionResultSet resultSet,
                                                          @NotNull PsiElement element) {
        return addDefinedVariables(variables, resultSet, element, TailTypes.noneType());
    }

    private Collection<LookupElement> addDefinedVariables(@NotNull Collection<DefinedVariable> variables,
                                                          @NotNull CompletionResultSet resultSet,
                                                          @NotNull PsiElement element,
                                                          @NotNull TailType tailType) {
        return variables.stream()
                        .filter(variable -> variable.isInScope(element))
                        .map(variable -> CompletionProviderUtils.addLookupElement(variable, Nodes.Variable, false, tailType, resultSet))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList();
    }

    private static final class RobotKeywordCallLocationIdentifier extends RecursiveRobotVisitor {

        private final int startOffsetInParent;

        private int currentIndex = -1;
        private int elementIndex = -1;

        private RobotKeywordCallLocationIdentifier(PsiElement sourceElement) {
            startOffsetInParent = sourceElement.getStartOffsetInParent();
        }

        @Override
        public void visitParameter(@NotNull RobotParameter o) {
            super.visitElement(o);
            ++currentIndex;

            int currentOffsetInParent = o.getStartOffsetInParent();
            updateFoundElementIndex(currentOffsetInParent);
        }

        @Override
        public void visitPositionalArgument(@NotNull RobotPositionalArgument o) {
            super.visitElement(o);
            ++currentIndex;

            int currentOffsetInParent = o.getStartOffsetInParent();
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
