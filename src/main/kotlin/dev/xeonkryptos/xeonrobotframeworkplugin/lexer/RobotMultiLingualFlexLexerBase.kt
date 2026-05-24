package dev.xeonkryptos.xeonrobotframeworkplugin.lexer

import com.intellij.openapi.project.Project
import com.intellij.psi.tree.IElementType
import dev.xeonkryptos.xeonrobotframeworkplugin.lexer.RobotLexingConstants.NON_BREAKING_SPACE_CHARACTER
import dev.xeonkryptos.xeonrobotframeworkplugin.lexer.RobotLexingConstants.SECTION_MARKER
import dev.xeonkryptos.xeonrobotframeworkplugin.lexer.RobotLexingConstants.SIMPLE_SPACE_CHARACTER
import dev.xeonkryptos.xeonrobotframeworkplugin.lexer.RobotLexingConstants.TAB_CHARACTER
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes

enum class RobotSectionType {
    SETTINGS, VARIABLES, TEST_CASES, TASKS, KEYWORDS, COMMENTS, INVALID
}

abstract class RobotMultiLingualFlexLexerBase @JvmOverloads constructor(protected val project: Project? = null) : RobotFlexLexerBase() {

    private val invalidSectionHandler = RobotMultiLingualStateHandler(RobotSectionType.INVALID, SimpleSectionStateSwitcher(RobotTypes.INVALID_SECTION_HEADER)::switchState)
    private val defaultLanguageSectionNames: Map<String, RobotMultiLingualStateHandler<RobotSectionType>> =
        mapOf("setting" to RobotMultiLingualStateHandler(RobotSectionType.SETTINGS, SimpleSectionStateSwitcher(RobotTypes.SETTINGS_HEADER)::switchState),
            "settings" to RobotMultiLingualStateHandler(RobotSectionType.SETTINGS, SimpleSectionStateSwitcher(RobotTypes.SETTINGS_HEADER)::switchState),
            "variable" to RobotMultiLingualStateHandler(RobotSectionType.VARIABLES, SimpleSectionStateSwitcher(RobotTypes.VARIABLES_HEADER)::switchState),
            "variables" to RobotMultiLingualStateHandler(RobotSectionType.VARIABLES, SimpleSectionStateSwitcher(RobotTypes.VARIABLES_HEADER)::switchState),
            "test case" to RobotMultiLingualStateHandler(RobotSectionType.TEST_CASES) { targetState ->
                resetInternalState()
                yybegin(targetState)
                pushbackEverythingUpToKeywordFinishedMarker()
                RobotTypes.TEST_CASES_HEADER_NAME
            },
            "test cases" to RobotMultiLingualStateHandler(RobotSectionType.TEST_CASES) { targetState ->
                resetInternalState()
                yybegin(targetState)
                pushbackEverythingUpToKeywordFinishedMarker()
                RobotTypes.TEST_CASES_HEADER_NAME
            },
            "task" to RobotMultiLingualStateHandler(RobotSectionType.TASKS) { targetState ->
                resetInternalState()
                yybegin(targetState)
                pushbackEverythingUpToKeywordFinishedMarker()
                RobotTypes.TASKS_HEADER_NAME
            },
            "tasks" to RobotMultiLingualStateHandler(RobotSectionType.TASKS) { targetState ->
                resetInternalState()
                yybegin(targetState)
                pushbackEverythingUpToKeywordFinishedMarker()
                RobotTypes.TASKS_HEADER_NAME
            },
            "keyword" to RobotMultiLingualStateHandler(RobotSectionType.KEYWORDS, SimpleSectionStateSwitcher(RobotTypes.USER_KEYWORDS_HEADER)::switchState),
            "keywords" to RobotMultiLingualStateHandler(RobotSectionType.KEYWORDS, SimpleSectionStateSwitcher(RobotTypes.USER_KEYWORDS_HEADER)::switchState),
            "comment" to RobotMultiLingualStateHandler(RobotSectionType.COMMENTS, SimpleSectionStateSwitcher(RobotTypes.COMMENTS_HEADER)::switchState),
            "comments" to RobotMultiLingualStateHandler(RobotSectionType.COMMENTS, SimpleSectionStateSwitcher(RobotTypes.COMMENTS_HEADER)::switchState))

    private val defaultLanguageGlobalSettingNames = mapOf(
        "library" to RobotTypes.LIBRARY_IMPORT_GLOBAL_SETTING,
        "resource" to RobotTypes.RESOURCE_IMPORT_GLOBAL_SETTING,
        "variables" to RobotTypes.VARIABLES_IMPORT_GLOBAL_SETTING,
        "name" to RobotTypes.SUITE_NAME_STATEMENT_GLOBAL_SETTING,
        "documentation" to RobotTypes.DOCUMENTATION_STATEMENT_GLOBAL_SETTING,
        "suite setup" to RobotTypes.SETUP_TEARDOWN_STATEMENTS_GLOBAL_SETTING,
        "suite teardown" to RobotTypes.SETUP_TEARDOWN_STATEMENTS_GLOBAL_SETTING,
        "metadata" to RobotTypes.METADATA_STATEMENT_GLOBAL_SETTING,
        "test tags" to RobotTypes.TAGS_STATEMENT_GLOBAL_SETTING,
        "test setup" to RobotTypes.SETUP_TEARDOWN_STATEMENTS_GLOBAL_SETTING,
        "test teardown" to RobotTypes.SETUP_TEARDOWN_STATEMENTS_GLOBAL_SETTING,
        "test timeout" to RobotTypes.TIMEOUT_STATEMENTS_GLOBAL_SETTING,
        "task setup" to RobotTypes.SETUP_TEARDOWN_STATEMENTS_GLOBAL_SETTING,
        "task teardown" to RobotTypes.SETUP_TEARDOWN_STATEMENTS_GLOBAL_SETTING,
        "task tags" to RobotTypes.TAGS_STATEMENT_GLOBAL_SETTING,
        "task timeout" to RobotTypes.TIMEOUT_STATEMENTS_GLOBAL_SETTING,
        "test template" to RobotTypes.TEMPLATE_STATEMENTS_GLOBAL_SETTING,
        "default tags" to RobotTypes.TAGS_STATEMENT_GLOBAL_SETTING,
    )

    protected var globalTemplateEnabled = false
    protected var localTemplateEnabled: Boolean = false
    protected var templateKeywordFound: Boolean = false

    protected var currentIndex: Int = -1

    protected val previousStates: IntArray = IntArray(20)

    protected fun switchSection(): IElementType {
        val sectionName = computeSectionName()
        val section = defaultLanguageSectionNames.getOrDefault(sectionName, invalidSectionHandler)
        val nextStateId = getSectionStateId(section.sourceType)
        return section.switchState(nextStateId)
    }

    private fun computeSectionName(): CharSequence {
        var index = 0
        val length = yylength()
        val sectionHeaderName = StringBuilder()
        while (index < length && (yycharat(index) == SECTION_MARKER)) index++
        while (index < length) {
            val c = yytext()[index]
            if (c == SECTION_MARKER || c == TAB_CHARACTER || isNewLineCharacter(c)) break
            if (isSuperSpace(index)) break
            sectionHeaderName.append(c.lowercase())
            index++
        }
        return sectionHeaderName.trim()
    }

    protected abstract fun getSectionStateId(sectionType: RobotSectionType): Int

    abstract fun yytext(): CharSequence

    fun isNewLineAt(currentTokenIndex: Int): Boolean = isNewLineCharacter(yycharat(currentTokenIndex))

    fun isNewLineCharacter(character: Char): Boolean = character == RobotLexingConstants.NEW_LINE || character == RobotLexingConstants.CARRIAGE_RETURN

    fun isSuperSpace(startingIndex: Int): Boolean = isSimpleSpaceCharacter(yycharat(startingIndex)) && startingIndex + 1 < yylength() && isSimpleSpaceCharacter(yycharat(startingIndex + 1))

    fun isSimpleSpaceCharacter(character: Char) = character == SIMPLE_SPACE_CHARACTER || character == NON_BREAKING_SPACE_CHARACTER

    protected fun enterNewState(newState: Int) {
        val previousState = yystate()
        ++currentIndex
        previousStates[currentIndex] = previousState
        yybegin(newState)
    }

    protected fun leaveState() {
        if (currentIndex >= 0) {
            val previousState = previousStates[currentIndex]
            --currentIndex
            yybegin(previousState)
        } else {
            yybegin(0) // 0 => YYINITIAL
        }
    }

    protected fun resetTemplateState() {
        localTemplateEnabled = globalTemplateEnabled
    }

    protected fun markTemplateParsingEnabled() {
        globalTemplateEnabled = true
        templateKeywordFound = true
        localTemplateEnabled = true
    }

    protected fun markLocalTemplateParsingEnabled() {
        localTemplateEnabled = true
    }

    protected fun markLocalTemplateParsingDisabled() {
        localTemplateEnabled = false
    }

    protected fun markTemplateKeywordFound() {
        templateKeywordFound = true
    }

    protected fun resetInternalState() {
        currentIndex = -1
        localTemplateEnabled = globalTemplateEnabled
    }

    /**
     * Resets the complete lexer including the additional internal states besides the lexer states from JFlex. You need to call this method when you want to
     * reset the lexer to the initial state completely, e.g. when starting to lex a new file.
     */
    protected fun resetLexer() {
        currentIndex = -1
        localTemplateEnabled = false
        templateKeywordFound = false
        globalTemplateEnabled = false
    }

    protected fun pushbackEverythingUpToKeywordFinishedMarker() {
        var lengthToKeep = 0
        var firstPossibleSpaceExitMarkerFound = false
        val totalLength = yylength()
        for (i in 0..<totalLength) {
            val currentChar = yycharat(i)
            if (currentChar == '\n' || currentChar == '\r' || currentChar == '\t') {
                break
            }
            if (Character.isWhitespace(currentChar)) {
                if (firstPossibleSpaceExitMarkerFound) {
                    lengthToKeep--
                    break
                }
                firstPossibleSpaceExitMarkerFound = true
            } else if (firstPossibleSpaceExitMarkerFound) {
                firstPossibleSpaceExitMarkerFound = false
            }
            lengthToKeep++
        }
        yypushback(totalLength - lengthToKeep)
    }

    private class RobotMultiLingualStateHandler<T>(val sourceType: T, private val switchStateCallable: (targetState: Int) -> IElementType) {

        fun switchState(targetState: Int): IElementType = switchStateCallable(targetState)
    }

    private inner class SimpleSectionStateSwitcher(private val elementType: IElementType) {

        fun switchState(targetState: Int): IElementType {
            resetInternalState()
            yybegin(targetState)
            return elementType
        }
    }
}
