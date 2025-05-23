package dev.xeonkryptos.xeonrobotframeworkplugin.psi.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.BracketSetting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Heading;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordDefinition;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordStatement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.PositionalArgument;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Setting;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.Variable;
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
        return position instanceof PositionalArgument || PsiTreeUtil.getParentOfType(position, PositionalArgument.class) != null;
    }

    private static boolean isVariable(@NotNull PsiElement position) {
        return position instanceof Variable || PsiTreeUtil.getParentOfType(position, Variable.class) != null;
    }

    /**
     * Determines if the given element is a part of the Settings header.
     *
     * @param position the element in question.
     *
     * @return true if this or one of its parents is the Settings header.
     */
    private static boolean isInSettings(@NotNull PsiElement position) {
        KeywordStatement keyword = getKeyword(position);
        if (keyword == null) {
            return false;
        }
        // and that keyword is in the test cases heading
        Heading heading = PsiTreeUtil.getParentOfType(keyword, Heading.class);
        return heading != null && heading.isSettings();
    }

    /**
     * Determines if the given element is a part of the Test Cases header.
     *
     * @param position the element in question.
     *
     * @return true if this or one of its parents is the Test Cases header.
     */
    private static boolean isInSameTestCase(@NotNull PsiElement sourceElement, @NotNull PsiElement position) {
        KeywordDefinition sourceKeywordDefinition = getKeywordDefinition(sourceElement);
        KeywordDefinition targetKeywordDefinition = getKeywordDefinition(position);
        return sourceKeywordDefinition == targetKeywordDefinition;
    }

    /**
     * Determines if the given element is a part of the Test Cases header.
     *
     * @param position the element in question.
     *
     * @return true if this or one of its parents is the Test Cases header.
     */
    private static boolean isInTestCase(@NotNull PsiElement position) {
        KeywordStatement keyword = getKeyword(position);
        if (keyword == null) {
            return false;
        }
        // and that keyword is in the test cases heading
        Heading heading = PsiTreeUtil.getParentOfType(keyword, Heading.class);
        return heading != null && heading.containsTestCases();
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
            KeywordStatement keyword = getKeyword(position);
            if (keyword == null) {
                return false;
            }
            PsiElement sibling = getPreviousStatement(keyword);
            if (sibling instanceof Setting) {
                return ((Setting) sibling).isSuiteTeardown();
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
            KeywordStatement keyword = getKeyword(position);
            if (keyword == null) {
                return false;
            }
            PsiElement sibling = getPreviousStatement(keyword);
            if (sibling instanceof Setting) {
                return ((Setting) sibling).isTestTeardown();
            }
        } else if (isInTestCase(position)) {
            // check that we are next to a teardown bracket setting
            KeywordStatement keyword = getKeyword(position);
            if (keyword == null) {
                return false;
            }
            PsiElement sibling = getPreviousStatement(keyword);
            if (sibling instanceof BracketSetting) {
                return ((BracketSetting) sibling).isTeardown();
            }
        }
        return false;
    }

    private static boolean isInKeywordStatement(@NotNull PsiElement position) {
        if (isInSettings(position)) {
            KeywordStatement keyword = getKeyword(position);
            return keyword != null;
        } else if (isInTestCase(position)) {
            KeywordStatement keyword = getKeyword(position);
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
        KeywordStatement keyword = getKeyword(position);
        if (keyword == null) {
            return false;
        }
        Heading heading = PsiTreeUtil.getParentOfType(keyword, Heading.class);
        if (heading != null && heading.containsKeywordDefinitions()) {
            PsiElement sibling = getPreviousStatement(keyword);
            if (sibling instanceof BracketSetting) {
                return ((BracketSetting) sibling).isTeardown();
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
    private static KeywordStatement getKeyword(@NotNull PsiElement position) {
        // either we are a keyword or we have a parent that is
        return position instanceof KeywordStatement keywordStatement ? keywordStatement : PsiTreeUtil.getParentOfType(position, KeywordStatement.class);
    }

    /**
     * Gets the keyword definition element associated with this element.
     *
     * @param position the element in question.
     *
     * @return either this element or one of its parents that is a keyword definition; else null.
     */
    @Nullable
    private static KeywordDefinition getKeywordDefinition(@NotNull PsiElement position) {
        return position instanceof KeywordDefinition keywordDefinition ? keywordDefinition : PsiTreeUtil.getParentOfType(position, KeywordDefinition.class);
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
