package dev.xeonkryptos.xeonrobotframeworkplugin.lexer

import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.Lexer
import com.intellij.lexer.MergingLexerAdapter
import com.intellij.openapi.project.Project
import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotExtendedVariableAccessLayerLexer
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenSets

open class MergingRobotLexerAdapter(original: Lexer) : MergingLexerAdapter(original, TokenSet.orSet(TokenSet.create(TokenType.BAD_CHARACTER), RobotTokenSets.REAL_WHITESPACE_SET))

class RobotLexerAdapter(project: Project? = null) : MergingRobotLexerAdapter(FlexAdapter(RobotLexerExtension(project)))

class RobotExtendedVariableAccessLayerAdapter : FlexAdapter(RobotExtendedVariableAccessLayerLexer())
