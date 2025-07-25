package dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.ide.config.RobotOptionsProvider;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableDto;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedKeyword;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.DefinedVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotUserKeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RobotSectionVariablesCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ResolverUtils {

    private static final Collection<String> VARIABLE_SETTERS = Set.of("set test variable", "set suite variable", "set global variable");

    private ResolverUtils() {
    }

    public static PsiElement findKeywordReference(@Nullable String keyword, @Nullable PsiFile psiFile) {
        if (keyword == null || !(psiFile instanceof RobotFile robotFile)) {
            return null;
        }

        for (DefinedKeyword definedKeyword : robotFile.getDefinedKeywords()) {
            if (definedKeyword.matches(keyword)) {
                return definedKeyword.reference();
            }
        }

        boolean includeTransitive = RobotOptionsProvider.getInstance(psiFile.getProject()).allowTransitiveImports();
        Collection<KeywordFile> importedFiles = robotFile.collectImportedFiles(includeTransitive);
        for (KeywordFile keywordFile : importedFiles) {
            for (DefinedKeyword definedKeyword : keywordFile.getDefinedKeywords()) {
                if (definedKeyword.matches(keyword)) {
                    return definedKeyword.reference();
                }
            }
        }
        return null;
    }

    @Nullable
    public static PsiElement findVariableElement(@Nullable String variableName, @Nullable PsiFile psiFile) {
        if (variableName == null || !(psiFile instanceof RobotFile robotFile)) {
            return null;
        }
        for (DefinedVariable definedVariable : robotFile.getDefinedVariables()) {
            if (definedVariable.matches(variableName)) {
                return definedVariable.reference();
            }
        }

        boolean includeTransitive = RobotOptionsProvider.getInstance(psiFile.getProject()).allowTransitiveImports();
        for (KeywordFile keywordFile : robotFile.collectImportedFiles(includeTransitive)) {
            if (keywordFile.getImportType() != ImportType.LIBRARY) {
                for (DefinedVariable definedVariable : keywordFile.getDefinedVariables()) {
                    if (definedVariable.matches(variableName)) {
                        return definedVariable.reference();
                    }
                }
            }
        }
        return null;
    }

    @NotNull
    public static List<DefinedVariable> walkKeyword(@Nullable RobotKeywordCall keywordCall) {
        if (keywordCall == null) {
            return Collections.emptyList();
        }

        String keywordName = keywordCall.getName();
        if (VARIABLE_SETTERS.contains(keywordName.toLowerCase())) {
            List<RobotPositionalArgument> positionalArgumentList = keywordCall.getPositionalArgumentList();
            if (!positionalArgumentList.isEmpty() && positionalArgumentList.getFirst().getFirstChild() instanceof RobotVariable variable) {
                String variableName = variable.getName();
                if (variableName != null) {
                    DefinedVariable definedVariable = new VariableDto(variable, variableName, null);
                    return Collections.singletonList(definedVariable);
                }
            }
            return Collections.emptyList();
        }

        List<DefinedVariable> variables = new ArrayList<>();
        PsiElement resolvedElement = keywordCall.getKeywordCallId().getReference().resolve();
        if (resolvedElement instanceof RobotUserKeywordStatement userKeywordStatement) {
            RobotSectionVariablesCollector variablesCollector = new RobotSectionVariablesCollector();
            userKeywordStatement.accept(variablesCollector);
            Collection<DefinedVariable> collectedVariables = variablesCollector.getVariables();
            variables.addAll(collectedVariables);
        }
        return variables;
    }
}
