package dev.xeonkryptos.xeonrobotframeworkplugin.psi.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto.VariableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ReservedVariable {

    // Scope Everywhere
    CURDIR("CURDIR", VariableScope.Global, VariableType.SCALAR), // An absolute path to the directory where the test data file is located. This variable is case-sensitive.
    TEMPDIR("TEMPDIR",
            VariableScope.Global,
            VariableType.SCALAR), // An absolute path to the system temporary directory. In UNIX-like systems this is typically /tmp, and in Windows c:\Documents and Settings\<user>\Local Settings\Temp.
    EXECDIR("EXECDIR", VariableScope.Global, VariableType.SCALAR), // An absolute path to the directory where test execution was started from.
    PATH_SEPARATOR("/", VariableScope.Global, VariableType.SCALAR), // The system directory path separator. / in UNIX-like systems and \ in Windows.
    PATH_ELEMENT(":", VariableScope.Global, VariableType.SCALAR), // The system path element separator. : in UNIX-like systems and ; in Windows.
    NEW_LINE("\\n", VariableScope.Global, VariableType.SCALAR), // The system line separator. \n in UNIX-like systems and \r\n in Windows.
    SPACE("SPACE", VariableScope.Global, VariableType.SCALAR), // String with space: " "
    SUPER_SPACE("SPACE * 2", VariableScope.Global, VariableType.SCALAR), // String with two spaces: "  "
    EMPTY("EMPTY", VariableScope.Global, VariableType.SCALAR), // Empty String
    EMPTY_LIST("EMPTY", VariableScope.Global, VariableType.LIST), // Empty list
    EMPTY_DICTIONARY("EMPTY", VariableScope.Global, VariableType.DICTIONARY), // Empty dictionary
    TRUE("True", VariableScope.Global, VariableType.SCALAR), // Boolean true
    FALSE("False", VariableScope.Global, VariableType.SCALAR), // Boolean false
    NONE("None", VariableScope.Global, VariableType.SCALAR), // Python None
    NULL("null", VariableScope.Global, VariableType.SCALAR), // Java null
    SUITE_NAME("SUITE_NAME", VariableScope.Global, VariableType.SCALAR), // The full name of the current test suite.
    SUITE_SOURCE("SUITE_SOURCE", VariableScope.Global, VariableType.SCALAR), // An absolute path to the suite file or directory.
    SUITE_DOCUMENTATION("SUITE_DOCUMENTATION",
                        VariableScope.Global,
                        VariableType.SCALAR), // The documentation of the current test suite. Can be set dynamically using using Set Suite Documentation keyword.
    SUITE_METADATA("SUITE_METADATA", VariableScope.Global, VariableType.DICTIONARY), // The free metadata of the current test suite. Can be set using Set Suite Metadata keyword.
    OUTPUT_DIR("OUTPUT_DIR", VariableScope.Global, VariableType.SCALAR), // An absolute path to the output directory.
    OUTPUT_FILE("OUTPUT_FILE", VariableScope.Global, VariableType.SCALAR), // An absolute path to the output file.
    REPORT_FILE("REPORT_FILE", VariableScope.Global, VariableType.SCALAR), // An absolute path to the report file or string NONE when no report is created.
    LOG_FILE("LOG_FILE", VariableScope.Global, VariableType.SCALAR), // An absolute path to the log file or string NONE when no log file is created.
    LOG_LEVEL("LOG_LEVEL", VariableScope.Global, VariableType.SCALAR), // Current log level.
    DEBUG_FILE("DEBUG_FILE", VariableScope.Global, VariableType.SCALAR), // An absolute path to the debug file or string NONE when no debug file is created.
    // Scope: Test Case
    PREV_TEST_NAME("PREV_TEST_NAME", VariableScope.TestCase, VariableType.SCALAR), // The name of the previous test case, or an empty string if no tests have been executed yet.
    PREV_TEST_STATUS("PREV_TEST_STATUS", VariableScope.TestCase, VariableType.SCALAR), // The status of the previous test case: either PASS, FAIL, or an empty string when no tests have been executed.
    PREV_TEST_MESSAGE("PREV_TEST_MESSAGE", VariableScope.TestCase, VariableType.SCALAR), // The possible error message of the previous test case.
    TEST_NAME("TEST_NAME", VariableScope.TestCase, VariableType.SCALAR), // The name of the current test case.
    TEST_TAGS("TEST_TAGS",
              VariableScope.TestCase,
              VariableType.LIST), // Contains the tags of the current test case in alphabetical order. Can be modified dynamically using Set Tags and Remove Tags keywords.
    TEST_DOCUMENTATION("TEST_DOCUMENTATION",
                       VariableScope.TestCase,
                       VariableType.SCALAR), // The documentation of the current test case. Can be set dynamically using using Set Test Documentation keyword.
    // Scope Test Teardown
    TEST_STATUS("TEST_STATUS", VariableScope.TestTeardown, VariableType.SCALAR), // The status of the current test case, either PASS or FAIL.
    TEST_MESSAGE("TEST_MESSAGE", VariableScope.TestTeardown, VariableType.SCALAR), // The message of the current test case.
    // Scope: Keyword Teardown
    KEYWORD_STATUS("KEYWORD_STATUS", VariableScope.KeywordTeardown, VariableType.SCALAR), // The status of the current keyword, either PASS or FAIL.
    KEYWORD_MESSAGE("KEYWORD_MESSAGE", VariableScope.KeywordTeardown, VariableType.SCALAR), // The possible error message of the current keyword.
    // Scope: Suite Teardown
    SUITE_STATUS("SUITE_STATUS", VariableScope.SuiteTeardown, VariableType.SCALAR), // The status of the current test suite, either PASS or FAIL.
    SUITE_MESSAGE("SUITE_MESSAGE", VariableScope.SuiteTeardown, VariableType.SCALAR) // The full message of the current test suite, including statistics.
    ;

    private final String variable;
    private final VariableScope scope;
    private final VariableType variableType;

    ReservedVariable(@NotNull String variable, @NotNull VariableScope scope, @NotNull VariableType variableType) {
        this.variable = variable;
        this.scope = scope;
        this.variableType = variableType;
    }

    @NotNull
    public final String getVariable() {
        return this.variable;
    }

    @NotNull
    public final VariableScope getScope() {
        return this.scope;
    }

    @NotNull
    public VariableType getVariableType() {
        return variableType;
    }

    @Nullable
    public final PsiElement getReferencedPsiElement(@NotNull Project project) {
        return VariableScope.getReferencedPsiElement(project);
    }
}
