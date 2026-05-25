package dev.xeonkryptos.xeonrobotframeworkplugin.lang

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.LayeredLexer
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import dev.xeonkryptos.xeonrobotframeworkplugin.lexer.RobotLexerAdapter
import dev.xeonkryptos.xeonrobotframeworkplugin.lexer.MergingRobotLexerAdapter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.ExtendedRobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.lexer.RobotExtendedVariableAccessLayerAdapter
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotParser
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTokenSets
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotFileImpl
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.stub.RobotStubFileElementType

class RobotParserDefinition : ParserDefinition {

    override fun createLexer(project: Project): Lexer {
        val layeredLexer = LayeredLexer(RobotLexerAdapter(project))
        layeredLexer.registerLayer(RobotExtendedVariableAccessLayerAdapter(), ExtendedRobotTypes.EXTENDED_VARIABLE_ACCESS_BODY)
        return MergingRobotLexerAdapter(layeredLexer)
    }

    override fun createParser(project: Project): PsiParser = RobotParser()

    override fun getFileNodeType(): IFileElementType = ROBOT_FILE

    override fun getWhitespaceTokens(): TokenSet = RobotTokenSets.WHITESPACE_SET

    override fun getCommentTokens(): TokenSet = RobotTokenSets.COMMENTS_SET

    override fun getStringLiteralElements(): TokenSet = RobotTokenSets.STRING_SET

    override fun createElement(node: ASTNode): PsiElement = try {
        RobotTypes.Factory.createElement(node)
    } catch (_: AssertionError) {
        ASTWrapperPsiElement(node)
    }

    override fun createFile(fileViewProvider: FileViewProvider): PsiFile = RobotFileImpl(fileViewProvider)
}

private val ROBOT_FILE: IFileElementType = RobotStubFileElementType()
