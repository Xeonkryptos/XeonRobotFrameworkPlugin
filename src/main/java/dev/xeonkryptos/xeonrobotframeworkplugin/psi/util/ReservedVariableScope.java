package dev.xeonkryptos.xeonrobotframeworkplugin.psi.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordCall;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotKeywordsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLocalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSettingsSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotSetupTeardownStatementsGlobalSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCaseStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotTestCasesSection;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ref.PythonResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ReservedVariableScope {
    Global {
        @Override
        public boolean isInScope(@NotNull PsiElement sourceElement, @NotNull PsiElement element) {
            // everywhere
            return true;
        }
    }, TestCase {
        @Override
        public final boolean isInScope(@NotNull PsiElement sourceElement, @NotNull PsiElement element) {
            // only in test cases
            return (isArgument(element) || isVariable(element)) && isInSameTestCase(sourceElement, element) || TestTeardown.isInScope(sourceElement, element);
        }
    }, KeywordTeardown {
        @Override
        public final boolean isInScope(@NotNull PsiElement sourceElement, @NotNull PsiElement element) {
            // only in teardown for keywords
            return (isArgument(element) || isVariable(element)) && isInKeywordTeardown(element);
        }
    }, TestTeardown {
        @Override
        public boolean isInScope(@NotNull PsiElement sourceElement, @NotNull PsiElement element) {
            // only in teardown for test cases
            return (isArgument(element) || isVariable(element)) && isInTestTeardown(element);
        }
    }, SuiteTeardown {
        @Override
        public boolean isInScope(@NotNull PsiElement sourceElement, @NotNull PsiElement element) {
            // only in teardown for suites
            return (isArgument(element) || isVariable(element)) && isInSuiteTeardown(element);
        }
    }, KeywordStatement {
        @Override
        public boolean isInScope(@NotNull PsiElement sourceElement, @NotNull PsiElement element) {
            return isArgument(element) && isInKeywordStatement(element);
        }
    };

    private static boolean isArgument(@NotNull PsiElement position) {
        return PsiTreeUtil.getParentOfType(position, RobotPositionalArgument.class, false) != null;
    }

    private static boolean isVariable(@NotNull PsiElement position) {
        return PsiTreeUtil.getParentOfType(position, RobotVariable.class, false) != null;
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
        RobotSettingsSection settingsSection = PsiTreeUtil.getParentOfType(keyword, RobotSettingsSection.class);
        return settingsSection != null;
    }

    /**
     * Determines if the given element is a part of the Test Cases header.
     *
     * @param position the element in question.
     *
     * @return true if this or one of its parents is the Test Cases header.
     */
    private static boolean isInSameTestCase(@NotNull PsiElement sourceElement, @NotNull PsiElement position) {
        RobotTestCaseStatement sourceTestCaseStatement = getTestCaseStatement(sourceElement);
        RobotTestCaseStatement targetTestCaseStatement = getTestCaseStatement(position);
        return sourceTestCaseStatement == targetTestCaseStatement;
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
        // and that keyword is in the test cases heading
        RobotTestCasesSection testCasesSection = PsiTreeUtil.getParentOfType(keyword, RobotTestCasesSection.class);
        return testCasesSection != null;
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
            // check that we are next to a suite teardown setting
            RobotKeywordCall keyword = getKeywordCall(position);
            if (keyword == null) {
                return false;
            }
            PsiElement sibling = getPreviousStatement(keyword);
            if (sibling instanceof RobotSetupTeardownStatementsGlobalSetting globalSetting) {
                return globalSetting.getText().equalsIgnoreCase("Suite Teardown");
            }
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
            PsiElement sibling = getPreviousStatement(keyword);
            if (sibling instanceof RobotSetupTeardownStatementsGlobalSetting) {
                return sibling.getText().equalsIgnoreCase("Test Teardown");
            }
        } else if (isInTestCase(position)) {
            // check that we are next to a teardown bracket setting
            RobotKeywordCall keyword = getKeywordCall(position);
            if (keyword == null) {
                return false;
            }
            PsiElement sibling = getPreviousStatement(keyword);
            if (sibling instanceof RobotLocalSetting localSetting) {
                return localSetting.getName().equalsIgnoreCase("Teardown");
            }
        }
        return false;
    }

    private static boolean isInKeywordStatement(@NotNull PsiElement position) {
        if (isInSettings(position)) {
            RobotKeywordCall keyword = getKeywordCall(position);
            return keyword != null;
        } else if (isInTestCase(position)) {
            RobotKeywordCall keyword = getKeywordCall(position);
            return keyword != null;
        }
        return false;
    }

    /**
     * Gets the previous statement of the given element ignoring whitespace.
     *
     * @param position the element in question.
     *
     * @return the previous statement (sibling) of the element or null if there not one.
     */
    @Nullable
    private static PsiElement getPreviousStatement(@NotNull PsiElement position) {
        PsiElement sibling = position.getPrevSibling();
        if (sibling instanceof PsiWhiteSpace) {
            sibling = sibling.getPrevSibling();
        }
        return sibling;
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
            PsiElement sibling = getPreviousStatement(keyword);
            if (sibling instanceof RobotLocalSetting localSetting) {
                return localSetting.getName().equalsIgnoreCase("Teardown");
            }
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

    /**
     * Gets the test case definition element associated with this element.
     *
     * @param position the element in question.
     *
     * @return either this element or one of its parents that is a test case definition; else null.
     */
    @Nullable
    private static RobotTestCaseStatement getTestCaseStatement(@NotNull PsiElement position) {
        return PsiTreeUtil.getParentOfType(position, RobotTestCaseStatement.class, false);
    }

    @Nullable
    public static PsiElement getReferencedPsiElement(@NotNull Project project) {
        // Robot 2.x
        PsiElement element = PythonResolver.findVariable("GLOBAL_VARIABLES", project);
        if (element == null) {
            // Robot 3.x
            element = PythonResolver.findClass("robot.variables.scopes.GlobalVariables", project);
        }
        return element;
    }

    public abstract boolean isInScope(@NotNull PsiElement sourceElement, @NotNull PsiElement element);
}
