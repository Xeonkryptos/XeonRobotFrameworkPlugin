package dev.xeonkryptos.xeonrobotframeworkplugin.psi.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.ImportType;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFile;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotScopeOwner;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSetupTeardownStatementsGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCasesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.reference.PythonResolver;
import dev.xeonkryptos.xeonrobotframeworkplugin.util.RobotNames;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum VariableScope {
    Global {
        @Override
        public boolean isInScope(@NotNull PsiElement sourceElement, @NotNull PsiElement element) {
            return true;
        }
    }, TestSuite {
        @Override
        public boolean isInScope(@NotNull PsiElement sourceElement, @NotNull PsiElement element) {
            PsiFile sourceContainingFile = sourceElement.getContainingFile();
            PsiFile elementContainingFile = element.getContainingFile();

            String sourceContainingFilePath = sourceContainingFile.getOriginalFile().getVirtualFile().getPath();
            String elementContainingFilePath = elementContainingFile.getOriginalFile().getVirtualFile().getPath();

            if (sourceContainingFilePath.equals(elementContainingFilePath)) {
                return true;
            }
            
            if (elementContainingFile instanceof RobotFile robotFile) {
                return robotFile.collectImportedFiles(true, ImportType.VARIABLES, ImportType.RESOURCE)
                                .stream()
                                .anyMatch(importedFile -> importedFile.getVirtualFile().getPath().equals(sourceContainingFilePath));
            }
            return false;
        }
    }, TestCase {
        @Override
        public final boolean isInScope(@NotNull PsiElement sourceElement, @NotNull PsiElement element) {
            return isInSameTestCase(sourceElement, element) || TestTeardown.isInScope(sourceElement, element);
        }
    }, Local {
        @Override
        public boolean isInScope(@NotNull PsiElement sourceElement, @NotNull PsiElement element) {
            RobotScopeOwner scopeOwner = PsiTreeUtil.getParentOfType(sourceElement, RobotScopeOwner.class);
            if (PsiTreeUtil.isAncestor(scopeOwner, element, false)) {
                return true;
            }
            PsiManager psiManager = sourceElement.getManager();
            RobotScopeOwner elementScopeOwner = PsiTreeUtil.getParentOfType(element, RobotScopeOwner.class);
            while (elementScopeOwner != null) {
                if (psiManager.areElementsEquivalent(scopeOwner, elementScopeOwner)) {
                    return true;
                }
                elementScopeOwner = PsiTreeUtil.getParentOfType(elementScopeOwner, RobotScopeOwner.class);
            }
            return false;
        }
    }, KeywordTeardown {
        @Override
        public final boolean isInScope(@NotNull PsiElement sourceElement, @NotNull PsiElement element) {
            return isInKeywordTeardown(element);
        }
    }, TestTeardown {
        @Override
        public boolean isInScope(@NotNull PsiElement sourceElement, @NotNull PsiElement element) {
            return isInTestTeardown(element);
        }
    }, SuiteTeardown {
        @Override
        public boolean isInScope(@NotNull PsiElement sourceElement, @NotNull PsiElement element) {
            return isInSuiteTeardown(element);
        }
    };

    /**
     * Determines if the given element is a part of the Test Cases header.
     *
     * @param position the element in question.
     *
     * @return true if this or one of its parents is the Test Cases header.
     */
    private static boolean isInSameTestCase(@NotNull PsiElement sourceElement, @NotNull PsiElement position) {
        RobotTestCaseStatement sourceTestCaseStatement = PsiTreeUtil.getParentOfType(sourceElement, RobotTestCaseStatement.class, false);
        RobotTestCaseStatement targetTestCaseStatement = PsiTreeUtil.getParentOfType(position, RobotTestCaseStatement.class, false);
        PsiManager psiManager = sourceElement.getManager();
        return psiManager.areElementsEquivalent(sourceTestCaseStatement, targetTestCaseStatement);
    }

    /**
     * Determines if the given element is a part of the suite teardown.
     *
     * @param position the element in question.
     *
     * @return true if this is part of the suite teardown.
     */
    private static boolean isInSuiteTeardown(@NotNull PsiElement position) {
        if (isInSettings(position)) {
            RobotKeywordCall keyword = getKeywordCall(position);
            if (keyword == null) {
                return false;
            }
            RobotSetupTeardownStatementsGlobalSetting globalSetting = PsiTreeUtil.getParentOfType(keyword, RobotSetupTeardownStatementsGlobalSetting.class);
            return globalSetting != null && RobotNames.SUITE_TEARDOWN_GLOBAL_SETTING_NAME.equalsIgnoreCase(globalSetting.getSettingName());
        }
        return false;
    }

    /**
     * Determines if the given element is a part of the test teardown.
     *
     * @param position the element in question.
     *
     * @return true if this is part of the test teardown.
     */
    private static boolean isInTestTeardown(@NotNull PsiElement position) {
        // this can either be in the settings of the file or the bracket settings of the definition
        if (isInSettings(position)) {
            // check that we are next to a test teardown setting
            RobotKeywordCall keyword = getKeywordCall(position);
            if (keyword == null) {
                return false;
            }
            RobotSetupTeardownStatementsGlobalSetting globalSetting = PsiTreeUtil.getParentOfType(keyword, RobotSetupTeardownStatementsGlobalSetting.class);
            return globalSetting != null && RobotNames.TEST_TEARDOWN_GLOBAL_SETTING_NAME.equalsIgnoreCase(globalSetting.getSettingName());
        } else if (isInTestCase(position)) {
            // check that we are next to a teardown bracket setting
            RobotKeywordCall keyword = getKeywordCall(position);
            if (keyword == null) {
                return false;
            }
            RobotLocalSetting localSetting = PsiTreeUtil.getParentOfType(keyword, RobotLocalSetting.class);
            return localSetting != null && RobotNames.TEARDOWN_LOCAL_SETTING_NAME.equalsIgnoreCase(localSetting.getSettingName());
        }
        return false;
    }

    /**
     * Determines if the given element is a part of the Settings header.
     *
     * @param position the element in question.
     *
     * @return true if this or one of its parents is the Settings header.
     */
    private static boolean isInSettings(@NotNull PsiElement position) {
        RobotKeywordCall keyword = getKeywordCall(position);
        if (keyword == null) {
            return false;
        }
        return PsiTreeUtil.getParentOfType(keyword, RobotSettingsSection.class) != null;
    }

    /**
     * Determines if the given element is a part of the Test Cases header.
     *
     * @param position the element in question.
     *
     * @return true if this or one of its parents is the Test Cases header.
     */
    private static boolean isInTestCase(@NotNull PsiElement position) {
        RobotKeywordCall keyword = getKeywordCall(position);
        if (keyword == null) {
            return false;
        }
        return PsiTreeUtil.getParentOfType(keyword, RobotTestCasesSection.class) != null;
    }

    /**
     * Determines if the given element is a part of the keyword teardown.
     *
     * @param position the element in question.
     *
     * @return true if this is part of the keyword teardown.
     */
    private static boolean isInKeywordTeardown(@NotNull PsiElement position) {
        // check that we are next to a teardown bracket setting
        RobotKeywordCall keyword = getKeywordCall(position);
        if (keyword == null) {
            return false;
        }
        RobotKeywordsSection keywordsSection = PsiTreeUtil.getParentOfType(keyword, RobotKeywordsSection.class);
        if (keywordsSection != null) {
            RobotLocalSetting localSetting = PsiTreeUtil.getParentOfType(keyword, RobotLocalSetting.class);
            return localSetting != null && RobotNames.TEARDOWN_LOCAL_SETTING_NAME.equalsIgnoreCase(localSetting.getSettingName());
        }
        return false;
    }

    /**
     * Gets the keyword statement element associated with this element.
     *
     * @param position the element in question.
     *
     * @return either this element or one of its parents that is a keyword statement; else null.
     */
    @Nullable
    private static RobotKeywordCall getKeywordCall(@NotNull PsiElement position) {
        // either we are a keyword or we have a parent that is
        return PsiTreeUtil.getParentOfType(position, RobotKeywordCall.class, false);
    }

    @Nullable
    public static PsiElement getReferencedPsiElement(@NotNull Project project) {
        return PythonResolver.findClass("robot.variables.scopes.GlobalVariables", project);
    }

    public abstract boolean isInScope(@NotNull PsiElement sourceElement, @NotNull PsiElement element);
}
