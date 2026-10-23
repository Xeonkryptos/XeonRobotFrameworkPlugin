package dev.xeonkryptos.xeonrobotframeworkplugin.lexer

import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.Lexer
import com.intellij.lexer.LayeredLexer
import com.intellij.lexer.MergingLexerAdapter
import com.intellij.openapi.project.Project
import com.intellij.psi.TokenType
import com.intellij.psi.tree.TokenSet
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ExtendedRobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotExtendedVariableAccessLayerLexer
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenSets

open class MergingRobotLexerAdapter(original: Lexer) : MergingLexerAdapter(original, TokenSet.orSet(TokenSet.create(TokenType.BAD_CHARACTER), RobotTokenSets.REAL_WHITESPACE_SET))

class RobotLexerAdapter(project: Project? = null) : MergingRobotLexerAdapter(FlexAdapter(RobotLexerExtension(project)))

class RobotExtendedVariableAccessLayerAdapter : FlexAdapter(RobotExtendedVariableAccessLayerLexer())

/** The lexer used for parsing and highlighting: the Robot lexer with a second layer splitting the content of item accesses into index, slice and literal. */
fun createLayeredRobotLexer(project: Project? = null): Lexer {
    val layeredLexer = LayeredLexer(RobotLexerAdapter(project))
    layeredLexer.registerLayer(RobotExtendedVariableAccessLayerAdapter(), ExtendedRobotTypes.EXTENDED_VARIABLE_ACCESS_BODY)
    return MergingRobotLexerAdapter(layeredLexer)
}
