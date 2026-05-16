package dev.xeonkryptos.xeonrobotframeworkplugin.lsp

import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes

object RobotLspTokenMapper {

    private val LOG = Logger.getInstance(RobotLspTokenMapper::class.java)

    private val CONTROL_FLOW_KEYWORDS = mapOf("FOR" to RobotTypes.FOR,
        "IF" to RobotTypes.IF,
        "ELSE IF" to RobotTypes.ELSE_IF,
        "ELSE" to RobotTypes.ELSE,
        "END" to RobotTypes.END,
        "WHILE" to RobotTypes.WHILE,
        "TRY" to RobotTypes.TRY,
        "EXCEPT" to RobotTypes.EXCEPT,
        "FINALLY" to RobotTypes.FINALLY,
        "RETURN" to RobotTypes.RETURN,
        "BREAK" to RobotTypes.BREAK,
        "CONTINUE" to RobotTypes.CONTINUE,
        "VAR" to RobotTypes.VAR,
        "GROUP" to RobotTypes.GROUP)

    private val BDD_PREFIXES = mapOf("given" to RobotTypes.GIVEN, "when" to RobotTypes.WHEN, "then" to RobotTypes.THEN, "and" to RobotTypes.AND, "but" to RobotTypes.BUT)

    private val FOR_SEPARATOR_KEYWORDS = mapOf("IN" to RobotTypes.FOR_IN, "IN RANGE" to RobotTypes.FOR_IN_RANGE, "IN ENUMERATE" to RobotTypes.FOR_IN_ENUMERATE, "IN ZIP" to RobotTypes.FOR_IN_ZIP)

    private val SETTING_IMPORT_KEYWORDS = mapOf("library" to RobotTypes.LIBRARY_IMPORT_KEYWORD, "resource" to RobotTypes.RESOURCE_IMPORT_KEYWORD, "variables" to RobotTypes.VARIABLES_IMPORT_KEYWORD)

    private val GLOBAL_SETTING_KEYWORDS: Map<String, IElementType> = buildMap {
        put("documentation", RobotTypes.DOCUMENTATION_KEYWORD)
        put("metadata", RobotTypes.METADATA_KEYWORD)
        put("suite name", RobotTypes.SUITE_NAME_KEYWORD)
        for (kw in listOf("suite setup", "suite teardown", "test setup", "test teardown", "task setup", "task teardown", "setup", "teardown")) {
            put(kw, RobotTypes.SETUP_TEARDOWN_STATEMENT_KEYWORDS)
        }
        for (kw in listOf("default tags", "force tags", "test tags", "task tags", "keyword tags", "tags")) {
            put(kw, RobotTypes.TAGS_KEYWORDS)
        }
        for (kw in listOf("test template", "task template", "template")) {
            put(kw, RobotTypes.TEMPLATE_KEYWORDS)
        }
        for (kw in listOf("test timeout", "task timeout", "timeout")) {
            put(kw, RobotTypes.TIMEOUT_KEYWORDS)
        }
    }

    fun mapAndFillGaps(semanticTokens: List<DecodedSemanticToken>, text: CharSequence, startOffset: Int, endOffset: Int): List<MappedToken> {
        val mapped = mutableListOf<MappedToken>()

        var previousToken: MappedToken? = null
        for (token in semanticTokens) {
            if (token.endOffset <= startOffset || token.startOffset >= endOffset) continue
            val tokenText = text.subSequence(token.startOffset.coerceAtLeast(startOffset), token.endOffset.coerceAtMost(endOffset))
            val tokenStart = token.startOffset.coerceAtLeast(startOffset)
            val mappedToken = mapSingleToken(token.tokenType, token.modifiers, tokenText, tokenStart, previousToken)
            mapped.add(mappedToken)
            previousToken = mappedToken
        }

        mapped.sortBy { it.startOffset }

        return fillGaps(mapped, text, startOffset, endOffset)
    }

    private fun mapSingleToken(tokenType: String, modifiers: Set<String>, text: CharSequence, startOffset: Int, previousToken: MappedToken?): MappedToken {
        return when (tokenType) {
            "headerSettings" -> MappedToken(RobotTypes.SETTINGS_HEADER, startOffset, text.length)
            "headerTestcase" -> MappedToken(RobotTypes.TEST_CASES_HEADER, startOffset, text.length)
            "headerTask" -> MappedToken(RobotTypes.TASKS_HEADER, startOffset, text.length)
            "headerKeyword" -> MappedToken(RobotTypes.USER_KEYWORDS_HEADER, startOffset, text.length)
            "headerVariable" -> MappedToken(RobotTypes.VARIABLES_HEADER, startOffset, text.length)
            "headerComment" -> MappedToken(RobotTypes.COMMENTS_HEADER, startOffset, text.length)

            "comment" -> MappedToken(RobotTypes.COMMENT, startOffset, text.length)

            "keyword" -> mapKeyword(text, modifiers, startOffset)

            "keywordCall", "keywordCallInner" -> mapKeywordCall(text, startOffset)

            "variable" -> MappedToken(RobotTypes.VARIABLE, startOffset, text.length)

            //"variableBegin" -> mapVariableBegin(text, startOffset)
            //"variableEnd" -> listOf(MappedToken(RobotTypes.VARIABLE_RBRACE, startOffset, text.length))

            "setting", "settingImport" -> mapSetting(text, startOffset)

            "argument", "argumentValue" -> if (previousToken?.elementType == RobotTypes.SETUP_TEARDOWN_STATEMENT_KEYWORDS) mapKeywordCall(text, startOffset)
            else MappedToken(RobotTypes.LITERAL_CONSTANT, startOffset, text.length)

            "separator" -> MappedToken(TokenType.WHITE_SPACE, startOffset, text.length)

            //"terminator" -> mapTerminator(text, startOffset)

            "continuation" -> MappedToken(RobotTypes.CONTINUATION, startOffset, text.length)

            "controlFlow" -> mapControlFlow(text, startOffset)

            "forSeparator" -> mapForSeparator(text, startOffset)

            "bddPrefix" -> mapBddPrefix(text, startOffset)

            "testcaseName" -> MappedToken(RobotTypes.TEST_CASE_NAME_PART, startOffset, text.length)
            "keywordName" -> {
                if ("declaration" in modifiers) MappedToken(RobotTypes.USER_KEYWORD_NAME_PART, startOffset, text.length)
                else MappedToken(RobotTypes.KEYWORD_NAME, startOffset, text.length)
            }

            "taskName" -> MappedToken(RobotTypes.TASK_NAME_PART, startOffset, text.length)

            "namespace" -> MappedToken(RobotTypes.LITERAL_CONSTANT, startOffset, text.length)

            "option" -> mapOption(text, startOffset)

            "namedArgument" -> mapNamedArgument(text, startOffset)

            "assignment", "assign" -> MappedToken(RobotTypes.ASSIGNMENT, startOffset, text.length)

            "documentation" -> MappedToken(RobotTypes.LITERAL_CONSTANT, startOffset, text.length)

            "expressionBegin" -> MappedToken(RobotTypes.PYTHON_EXPRESSION_START, startOffset, text.length)
            "expressionEnd" -> MappedToken(RobotTypes.PYTHON_EXPRESSION_END, startOffset, text.length)
            "expression", "variableExpression" -> MappedToken(RobotTypes.PYTHON_EXPRESSION_CONTENT, startOffset, text.length)

            "error" -> MappedToken(TokenType.BAD_CHARACTER, startOffset, text.length)

            "name" -> MappedToken(RobotTypes.LITERAL_CONSTANT, startOffset, text.length)

            "operator" -> mapOperator(text, startOffset)

            else -> {
                LOG.warn("Unknown LSP semantic token type: $tokenType (text: '$text')")
                MappedToken(RobotTypes.LITERAL_CONSTANT, startOffset, text.length)
            }
        }
    }

    private fun mapKeyword(text: CharSequence, modifiers: Set<String>, startOffset: Int): MappedToken {
        val textStr = text.toString()

        CONTROL_FLOW_KEYWORDS[textStr.uppercase()]?.let {
            return MappedToken(it, startOffset, text.length)
        }

        if ("definition" in modifiers || "declaration" in modifiers) {
            return MappedToken(RobotTypes.USER_KEYWORD_NAME_PART, startOffset, text.length)
        }

        return MappedToken(RobotTypes.KEYWORD_NAME, startOffset, text.length)
    }

    private fun mapKeywordCall(text: CharSequence, startOffset: Int): MappedToken { //val dotIndex = text.indexOf('.')
        //if (dotIndex > 0 && dotIndex < text.length - 1) {
        //    return listOf(
        //        MappedToken(RobotTypes.KEYWORD_LIBRARY_NAME, startOffset, dotIndex),
        //        MappedToken(RobotTypes.KEYWORD_LIBRARY_SEPARATOR, startOffset + dotIndex, 1),
        //        MappedToken(RobotTypes.KEYWORD_NAME, startOffset + dotIndex + 1, text.length - dotIndex - 1)
        //    )
        //}
        return MappedToken(RobotTypes.KEYWORD_NAME, startOffset, text.length)
    }

    private fun mapVariableBegin(text: CharSequence, startOffset: Int): List<MappedToken> {
        if (text.length >= 2) {
            val startType = when (text[0]) {
                '$' -> RobotTypes.SCALAR_VARIABLE_START
                '@' -> RobotTypes.LIST_VARIABLE_START
                '&' -> RobotTypes.DICT_VARIABLE_START
                '%' -> RobotTypes.ENV_VARIABLE_START
                else -> return listOf(MappedToken(RobotTypes.LITERAL_CONSTANT, startOffset, text.length))
            }
            return listOf(MappedToken(startType, startOffset, 1), MappedToken(RobotTypes.VARIABLE_LBRACE, startOffset + 1, 1))
        }
        return listOf(MappedToken(RobotTypes.LITERAL_CONSTANT, startOffset, text.length))
    }

    private fun mapSetting(text: CharSequence, startOffset: Int): MappedToken {
        val textStr = text.toString()

        //if (textStr.startsWith("[") && textStr.endsWith("]")) {
        //    return listOf(
        //        MappedToken(RobotTypes.LOCAL_SETTING_START, startOffset, 1),
        //        MappedToken(RobotTypes.LOCAL_SETTING_NAME, startOffset + 1, text.length - 2),
        //        MappedToken(RobotTypes.LOCAL_SETTING_END, startOffset + text.length - 1, 1)
        //    )
        //}

        val lower = textStr.lowercase().trim()

        SETTING_IMPORT_KEYWORDS[lower]?.let {
            return MappedToken(it, startOffset, text.length)
        }

        GLOBAL_SETTING_KEYWORDS[lower]?.let {
            return MappedToken(it, startOffset, text.length)
        }

        return MappedToken(RobotTypes.UNKNOWN_SETTING_KEYWORD, startOffset, text.length)
    }

    private fun mapTerminator(text: CharSequence, startOffset: Int): List<MappedToken> {
        val tokens = mutableListOf<MappedToken>()
        var i = 0
        while (i < text.length) {
            when (text[i]) {
                '\r' -> {
                    val len = if (i + 1 < text.length && text[i + 1] == '\n') 2 else 1
                    tokens.add(MappedToken(RobotTypes.EOL, startOffset + i, len))
                    i += len
                }

                '\n' -> {
                    tokens.add(MappedToken(RobotTypes.EOL, startOffset + i, 1))
                    i++
                }

                else -> {
                    val wsStart = i
                    while (i < text.length && text[i] != '\r' && text[i] != '\n') i++
                    tokens.add(MappedToken(TokenType.WHITE_SPACE, startOffset + wsStart, i - wsStart))
                }
            }
        }
        return tokens.ifEmpty { listOf(MappedToken(RobotTypes.EOS, startOffset, text.length)) }
    }

    private fun mapControlFlow(text: CharSequence, startOffset: Int): MappedToken {
        val elementType = CONTROL_FLOW_KEYWORDS[text.toString().uppercase()] ?: RobotTypes.KEYWORD_NAME
        return MappedToken(elementType, startOffset, text.length)
    }

    private fun mapForSeparator(text: CharSequence, startOffset: Int): MappedToken {
        val elementType = FOR_SEPARATOR_KEYWORDS[text.toString().uppercase().trim()] ?: RobotTypes.FOR_IN
        return MappedToken(elementType, startOffset, text.length)
    }

    private fun mapBddPrefix(text: CharSequence, startOffset: Int): MappedToken {
        val elementType = BDD_PREFIXES[text.toString().lowercase().trim()] ?: RobotTypes.GIVEN
        return MappedToken(elementType, startOffset, text.length)
    }

    private fun mapOption(text: CharSequence, startOffset: Int): MappedToken {
        val upper = text.toString().uppercase().trim()
        return when (upper) {
            "WITH NAME", "AS" -> MappedToken(RobotTypes.WITH_NAME, startOffset, text.length)
            else -> MappedToken(RobotTypes.LITERAL_CONSTANT, startOffset, text.length)
        }
    }

    private fun mapNamedArgument(text: CharSequence, startOffset: Int): MappedToken { //val equalsIdx = text.indexOf('=')
        //if (equalsIdx > 0) {
        //    val tokens = mutableListOf<MappedToken>()
        //    tokens.add(MappedToken(RobotTypes.PARAMETER_NAME, startOffset, equalsIdx))
        //    tokens.add(MappedToken(RobotTypes.PARAMETER_ASSIGNMENT, startOffset + equalsIdx, 1))
        //    if (equalsIdx + 1 < text.length) {
        //        tokens.add(MappedToken(RobotTypes.LITERAL_CONSTANT, startOffset + equalsIdx + 1, text.length - equalsIdx - 1))
        //    }
        //    return tokens
        //}
        return MappedToken(RobotTypes.PARAMETER_NAME, startOffset, text.length)
    }

    private fun mapOperator(text: CharSequence, startOffset: Int): MappedToken = when (text) {
        "[" -> MappedToken(RobotTypes.LOCAL_SETTING_START, startOffset, text.length)
        "]" -> MappedToken(RobotTypes.LOCAL_SETTING_END, startOffset, text.length)
        else -> MappedToken(RobotTypes.LITERAL_CONSTANT, startOffset, text.length)
    }

    private fun fillGaps(tokens: List<MappedToken>, text: CharSequence, startOffset: Int, endOffset: Int): List<MappedToken> {
        if (tokens.isEmpty()) {
            return createGapTokens(text, startOffset, endOffset)
        }

        val result = mutableListOf<MappedToken>()
        var currentPos = startOffset

        for (token in tokens) {
            if (token.startOffset > currentPos) {
                result.addAll(createGapTokens(text, currentPos, token.startOffset))
            }
            result.add(token)
            currentPos = token.startOffset + token.length
        }

        if (currentPos < endOffset) {
            result.addAll(createGapTokens(text, currentPos, endOffset))
        }

        return result
    }

    private fun createGapTokens(text: CharSequence, startOffset: Int, endOffset: Int): List<MappedToken> {
        if (startOffset >= endOffset || startOffset >= text.length) return emptyList()

        val tokens = mutableListOf<MappedToken>()
        var i = startOffset
        val end = endOffset.coerceAtMost(text.length)

        while (i < end) {
            when (text[i]) {
                '\r' -> {
                    val len = if (i + 1 < end && text[i + 1] == '\n') 2 else 1
                    tokens.add(MappedToken(RobotTypes.EOL, i, len))
                    i += len
                }

                '\n' -> {
                    tokens.add(MappedToken(RobotTypes.EOL, i, 1))
                    i++
                }

                ' ', '\t' -> {
                    val wsStart = i
                    while (i < end && (text[i] == ' ' || text[i] == '\t')) i++
                    tokens.add(MappedToken(TokenType.WHITE_SPACE, wsStart, i - wsStart))
                }

                else -> {
                    val textStart = i
                    while (i < end && text[i] != '\r' && text[i] != '\n' && text[i] != ' ' && text[i] != '\t') i++
                    tokens.add(MappedToken(RobotTypes.LITERAL_CONSTANT, textStart, i - textStart))
                }
            }
        }

        return tokens
    }
}
