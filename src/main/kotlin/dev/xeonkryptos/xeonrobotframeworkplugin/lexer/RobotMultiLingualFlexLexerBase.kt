package dev.xeonkryptos.xeonrobotframeworkplugin.lexer

import com.intellij.openapi.project.Project
import com.intellij.psi.tree.IElementType
import dev.xeonkryptos.xeonrobotframeworkplugin.lexer.RobotLexingConstants.NON_BREAKING_SPACE_CHARACTER
import dev.xeonkryptos.xeonrobotframeworkplugin.lexer.RobotLexingConstants.SECTION_MARKER
import dev.xeonkryptos.xeonrobotframeworkplugin.lexer.RobotLexingConstants.SIMPLE_SPACE_CHARACTER
import dev.xeonkryptos.xeonrobotframeworkplugin.lexer.RobotLexingConstants.TAB_CHARACTER
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTemplateKeywordLexer
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes.LOCAL_SETTING_START
import kotlin.math.max

enum class RobotSectionType {
    SETTINGS, VARIABLES, TEST_CASES, TASKS, KEYWORDS, COMMENTS, INVALID
}

enum class RobotGlobalSettingType {
    SIMPLE_VALUE_SETTING, CONFIGURABLE_SETTING, KEYWORD_CALL_SETTING
}

enum class RobotLocalSettingType {
    SIMPLE_VALUE_SETTING, KEYWORD_CALL_SETTING, PARAMETER_DEFINITION_SETTING
}

enum class RobotKeywordType {
    BEHAVIOUR_DRIVEN, NORMAL_KEYWORD
}

abstract class RobotMultiLingualFlexLexerBase @JvmOverloads constructor(protected val project: Project? = null) : RobotFlexLexerBase() {

    private val invalidSectionHandler = RobotMultiLingualStateHandler(RobotSectionType.INVALID, SimpleSectionStateSwitcher(RobotTypes.INVALID_SECTION_HEADER)::switchState)
    private val invalidGlobalSettingHandler =
        RobotMultiLingualStateHandler(RobotGlobalSettingType.SIMPLE_VALUE_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.UNKNOWN_SETTING_KEYWORD)::switchState)
    private val invalidLocalSettingHandler = RobotMultiLingualStateHandler(RobotLocalSettingType.SIMPLE_VALUE_SETTING, SimpleLocalSettingStateSwitcher()::switchState)
    private val intermediateTemplateLocalSettingHandler = RobotMultiLingualStateHandler(RobotLocalSettingType.SIMPLE_VALUE_SETTING, LocalTemplateSettingStateSwitcher()::switchState)
    private val normalKeywordHandler = RobotMultiLingualStateHandler(RobotKeywordType.NORMAL_KEYWORD, SimpleKeywordStateSwitcher()::switchState)

    private val defaultLanguageSectionNames = mapOf("setting" to RobotMultiLingualStateHandler(RobotSectionType.SETTINGS, SimpleSectionStateSwitcher(RobotTypes.SETTINGS_HEADER)::switchState),
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
        "library" to RobotMultiLingualStateHandler(RobotGlobalSettingType.CONFIGURABLE_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.LIBRARY_IMPORT_KEYWORD)::switchState),
        "resource" to RobotMultiLingualStateHandler(RobotGlobalSettingType.SIMPLE_VALUE_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.RESOURCE_IMPORT_KEYWORD)::switchState),
        "variables" to RobotMultiLingualStateHandler(RobotGlobalSettingType.CONFIGURABLE_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.VARIABLES_IMPORT_KEYWORD)::switchState),
        "name" to RobotMultiLingualStateHandler(RobotGlobalSettingType.SIMPLE_VALUE_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.SUITE_NAME_KEYWORD)::switchState),
        "documentation" to RobotMultiLingualStateHandler(RobotGlobalSettingType.SIMPLE_VALUE_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.DOCUMENTATION_KEYWORD)::switchState),
        "suite setup" to RobotMultiLingualStateHandler(RobotGlobalSettingType.KEYWORD_CALL_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.SETUP_TEARDOWN_STATEMENT_KEYWORDS)::switchState),
        "suite teardown" to RobotMultiLingualStateHandler(RobotGlobalSettingType.KEYWORD_CALL_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.SETUP_TEARDOWN_STATEMENT_KEYWORDS)::switchState),
        "metadata" to RobotMultiLingualStateHandler(RobotGlobalSettingType.CONFIGURABLE_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.METADATA_KEYWORD)::switchState),
        "test tags" to RobotMultiLingualStateHandler(RobotGlobalSettingType.SIMPLE_VALUE_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.TAGS_KEYWORDS)::switchState),
        "test setup" to RobotMultiLingualStateHandler(RobotGlobalSettingType.KEYWORD_CALL_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.SETUP_TEARDOWN_STATEMENT_KEYWORDS)::switchState),
        "test teardown" to RobotMultiLingualStateHandler(RobotGlobalSettingType.KEYWORD_CALL_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.SETUP_TEARDOWN_STATEMENT_KEYWORDS)::switchState),
        "test timeout" to RobotMultiLingualStateHandler(RobotGlobalSettingType.SIMPLE_VALUE_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.TIMEOUT_KEYWORDS)::switchState),
        "test template" to RobotMultiLingualStateHandler(RobotGlobalSettingType.KEYWORD_CALL_SETTING) { targetState ->
            markTemplateParsingEnabled()
            SimpleGlobalSettingStateSwitcher(RobotTypes.TEMPLATE_KEYWORDS).switchState(targetState)
        },
        "task tags" to RobotMultiLingualStateHandler(RobotGlobalSettingType.SIMPLE_VALUE_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.TAGS_KEYWORDS)::switchState),
        "task setup" to RobotMultiLingualStateHandler(RobotGlobalSettingType.KEYWORD_CALL_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.SETUP_TEARDOWN_STATEMENT_KEYWORDS)::switchState),
        "task teardown" to RobotMultiLingualStateHandler(RobotGlobalSettingType.KEYWORD_CALL_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.SETUP_TEARDOWN_STATEMENT_KEYWORDS)::switchState),
        "task timeout" to RobotMultiLingualStateHandler(RobotGlobalSettingType.SIMPLE_VALUE_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.TIMEOUT_KEYWORDS)::switchState),
        "task template" to RobotMultiLingualStateHandler(RobotGlobalSettingType.KEYWORD_CALL_SETTING) { targetState ->
            markTemplateParsingEnabled()
            SimpleGlobalSettingStateSwitcher(RobotTypes.TEMPLATE_KEYWORDS).switchState(targetState)
        },
        "keyword tags" to RobotMultiLingualStateHandler(RobotGlobalSettingType.SIMPLE_VALUE_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.TAGS_KEYWORDS)::switchState),
        "default tags" to RobotMultiLingualStateHandler(RobotGlobalSettingType.SIMPLE_VALUE_SETTING, SimpleGlobalSettingStateSwitcher(RobotTypes.TAGS_KEYWORDS)::switchState),
    )

    private val defaultLanguageLocalSettingNames = mapOf("tags" to RobotMultiLingualStateHandler(RobotLocalSettingType.SIMPLE_VALUE_SETTING, SimpleLocalSettingStateSwitcher()::switchState),
        "setup" to RobotMultiLingualStateHandler(RobotLocalSettingType.KEYWORD_CALL_SETTING, SimpleLocalSettingStateSwitcher()::switchState),
        "teardown" to RobotMultiLingualStateHandler(RobotLocalSettingType.KEYWORD_CALL_SETTING, SimpleLocalSettingStateSwitcher()::switchState),
        "timeout" to RobotMultiLingualStateHandler(RobotLocalSettingType.SIMPLE_VALUE_SETTING, SimpleLocalSettingStateSwitcher()::switchState),
        "arguments" to RobotMultiLingualStateHandler(RobotLocalSettingType.PARAMETER_DEFINITION_SETTING, SimpleLocalSettingStateSwitcher()::switchState))

    private val defaultLanguageBehaviourDrivenIdentifierNames =
        mapOf("given" to RobotMultiLingualStateHandler(RobotKeywordType.BEHAVIOUR_DRIVEN, SimpleBehaviourDrivenIdentifierStateSwitcher(RobotTypes.GIVEN)::switchState),
            "when" to RobotMultiLingualStateHandler(RobotKeywordType.BEHAVIOUR_DRIVEN, SimpleBehaviourDrivenIdentifierStateSwitcher(RobotTypes.WHEN)::switchState),
            "then" to RobotMultiLingualStateHandler(RobotKeywordType.BEHAVIOUR_DRIVEN, SimpleBehaviourDrivenIdentifierStateSwitcher(RobotTypes.THEN)::switchState),
            "and" to RobotMultiLingualStateHandler(RobotKeywordType.BEHAVIOUR_DRIVEN, SimpleBehaviourDrivenIdentifierStateSwitcher(RobotTypes.AND)::switchState),
            "but" to RobotMultiLingualStateHandler(RobotKeywordType.BEHAVIOUR_DRIVEN, SimpleBehaviourDrivenIdentifierStateSwitcher(RobotTypes.BUT)::switchState))

    protected var globalTemplateEnabled = false
    protected var localTemplateEnabled: Boolean = false
    protected var templateKeywordFound: Boolean = false

    protected var currentIndex: Int = -1

    protected val previousStates: IntArray = IntArray(20)

    protected var buffer: CharSequence = ""
    protected var endPosition: Int = 0

    protected fun switchSection(): IElementType? {
        val sectionName = computeSectionName()
        val section = defaultLanguageSectionNames.getOrDefault(sectionName, invalidSectionHandler)
        val nextStateId = getSectionStateId(section.sourceType)
        return section.switchState(nextStateId)
    }

    private fun computeSectionName(): CharSequence {
        var index = 0
        val length = yylength()
        val sectionHeaderName = StringBuilder()
        while (index < length && yycharat(index) == SECTION_MARKER) index++
        while (index < length) {
            val c = yytext()[index]
            if (c == SECTION_MARKER || c == TAB_CHARACTER || isNewLineCharacter(c) || isSuperSpace(index)) break
            sectionHeaderName.append(c.lowercase())
            index++
        }
        return sectionHeaderName.trim()
    }

    protected fun switchGlobalSetting(): IElementType? {
        val settingName = computeGlobalSettingName()
        val globalSetting = defaultLanguageGlobalSettingNames.getOrDefault(settingName, invalidGlobalSettingHandler)
        val nextStateId = getGlobalSettingStateId(globalSetting.sourceType)
        return globalSetting.switchState(nextStateId)
    }

    private fun computeGlobalSettingName(): CharSequence {
        var index = 0
        val length = yylength()
        val globalSettingName = StringBuilder()
        while (index < length) {
            val c = yytext()[index]
            if (isSuperSpace(index) || c == TAB_CHARACTER || isNewLineCharacter(c)) break
            globalSettingName.append(c.lowercase())
            index++
        }
        return globalSettingName.trim()
    }

    protected fun switchLocalSetting(): IElementType? {
        val localSettingName = computeLocalSettingName()
        val localSetting = defaultLanguageLocalSettingNames.getOrDefault(localSettingName, intermediateTemplateLocalSettingHandler)
        val nextStateId = getLocalSettingStateId(localSetting.sourceType)
        return localSetting.switchState(nextStateId)
    }

    private fun computeLocalSettingName(): CharSequence {
        val sourceText = yytext()
        return sourceText.substring(1, sourceText.length - 1).trim()
    }

    protected fun switchPotentialKeyword(): IElementType? {
        val keywordName = yytext()
        val stateHandler = defaultLanguageBehaviourDrivenIdentifierNames.getOrDefault(keywordName, normalKeywordHandler)
        val nextStateId = getKeywordStateId(stateHandler.sourceType)
        return stateHandler.switchState(nextStateId)
    }

    protected abstract fun getSectionStateId(sectionType: RobotSectionType): Int

    protected abstract fun getGlobalSettingStateId(settingType: RobotGlobalSettingType): Int

    protected abstract fun getLocalSettingDefinitionState(): Int

    protected abstract fun getLocalSettingStateId(settingType: RobotLocalSettingType): Int

    protected abstract fun getNextTemplateStates(parseResult: TemplateParseResult): IntArray

    protected abstract fun isTemplateSupportingState(state: Int): Boolean

    protected abstract fun getKeywordStateId(keywordType: RobotKeywordType): Int

    abstract fun yytext(): CharSequence

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
        val pushbackLength = max(0, totalLength - lengthToKeep - 1)
        yypushback(pushbackLength)
    }

    private class RobotMultiLingualStateHandler<T>(val sourceType: T, private val switchStateCallable: (targetState: Int) -> IElementType?) {

        fun switchState(targetState: Int): IElementType? = switchStateCallable(targetState)
    }

    private inner class SimpleSectionStateSwitcher(private val elementType: IElementType) {

        fun switchState(targetState: Int): IElementType {
            resetInternalState()
            yybegin(targetState)
            return elementType
        }
    }

    private inner class SimpleGlobalSettingStateSwitcher(private val elementType: IElementType) {

        fun switchState(targetState: Int): IElementType {
            enterNewState(targetState)
            pushBackTrailingWhitespace()
            return elementType
        }
    }

    private inner class SimpleLocalSettingStateSwitcher {

        fun switchState(targetState: Int): IElementType {
            yypushback(yylength() - 1)
            enterNewState(targetState)
            enterNewState(getLocalSettingDefinitionState())
            return LOCAL_SETTING_START
        }
    }

    private inner class LocalTemplateSettingStateSwitcher {

        fun switchState(targetState: Int): IElementType? {
            val localSettingName = computeLocalSettingName()
            if (localSettingName != "template" || isTemplateSupportingState(yystate())) return invalidLocalSettingHandler.switchState(targetState)

            val lexer = RobotTemplateKeywordLexer()
            lexer.reset(buffer, tokenEnd, endPosition, 0)
            val parseResult = lexer.advance() ?: return invalidLocalSettingHandler.switchState(targetState)

            yypushback(yylength() - 1)
            for (state in getNextTemplateStates(parseResult)) {
                enterNewState(state)
            }
            markLocalTemplateParsingDisabled()
            return LOCAL_SETTING_START
        }
    }

    private inner class SimpleBehaviourDrivenIdentifierStateSwitcher(private val elementType: IElementType) {

        fun switchState(targetState: Int): IElementType {
            pushBackTrailingWhitespace()
            enterNewState(targetState)
            return elementType
        }
    }

    private inner class SimpleKeywordStateSwitcher {

        fun switchState(targetState: Int): IElementType? {
            enterNewState(targetState)
            yypushback(yylength())
            return null
        }
    }
}

enum class TemplateParseResult {
    EMPTY_RESET, NONE_RESET, KEYWORD
}
