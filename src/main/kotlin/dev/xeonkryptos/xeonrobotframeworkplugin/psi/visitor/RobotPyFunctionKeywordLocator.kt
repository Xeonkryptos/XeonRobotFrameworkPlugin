package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor

import com.intellij.lang.LighterAST
import com.intellij.lang.LighterASTNode
import com.intellij.psi.impl.source.tree.RecursiveLighterASTNodeWalkingVisitor
import com.jetbrains.python.PyElementTypes
import com.jetbrains.python.PyTokenTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.util.KeywordNameUtil
import java.util.ArrayDeque
import java.util.regex.Pattern

class RobotPyFunctionKeywordLocator(private val ast: LighterAST, private val fileText: CharSequence) : RecursiveLighterASTNodeWalkingVisitor(ast) {

    private val autoKeywordsStack = ArrayDeque<Boolean>().apply { push(true) }

    private val mutableKeywordPythonFunctionOccurrences: MutableMap<String, MutableList<Int>> = mutableMapOf()
    val keywordPythonFunctionOccurrences: Map<String, MutableList<Int>> get() = mutableKeywordPythonFunctionOccurrences

    private val assignRobotAutoKeywords = Pattern.compile("""ROBOT_AUTO_KEYWORDS\s*=\s*(True|False)""")
    private val decoratorAutoKeywords = Pattern.compile("""auto_keywords\s*=\s*(True|False)""")
    private val decoratorKeywordName = Pattern.compile("""@keyword\s*\(\s*(?:name\s*=)?\s*(["'])(.*?)\1""")
    private val decoratorRobotNameAttr = Pattern.compile("""robot_name\s*=\s*(["'])(.*?)\1""")
    private val decoratorNotKeyword = Pattern.compile("""@not_keyword\b""")
    private val decoratorKeywordBare = Pattern.compile("""@keyword\b""")

    override fun visitNode(node: LighterASTNode) {
        when (node.tokenType) {
            PyElementTypes.CLASS_DECLARATION -> {
                processClass(node)
                return
            }

            PyElementTypes.FUNCTION_DECLARATION -> processFunction(node)
        }
        super.visitNode(node)
    }

    private fun processClass(classNode: LighterASTNode) {
        val previous = autoKeywordsStack.peek()
        val localAuto = computeClassAutoKeywords(classNode, previous)
        autoKeywordsStack.push(localAuto)
        for (child in ast.getChildren(classNode)) {
            visitNode(child)
        }
        autoKeywordsStack.pop()
    }

    private fun computeClassAutoKeywords(classNode: LighterASTNode, inherited: Boolean): Boolean {
        var value = inherited
        val text = slice(classNode)
        val mAssign = assignRobotAutoKeywords.matcher(text)
        while (mAssign.find()) {
            value = mAssign.group(1) == "True"
        }
        val decoratorList = findDecoratorListChild(classNode)
        if (decoratorList != null) {
            val decText = slice(decoratorList)
            if (decText.contains("@library")) {
                val m = decoratorAutoKeywords.matcher(decText)
                if (m.find()) {
                    value = m.group(1) == "True"
                }
            }
        }
        return value
    }

    private fun processFunction(funcNode: LighterASTNode) {
        val name = extractFunctionName(funcNode) ?: return
        val decoratorList = findDecoratorListChild(funcNode)
        val decoratorsText = decoratorList?.let { slice(it) } ?: ""
        if (decoratorNotKeyword.matcher(decoratorsText).find()) return

        val mKeywordName = decoratorKeywordName.matcher(decoratorsText)
        if (mKeywordName.find()) {
            val custom = mKeywordName.group(2)
            register(custom, funcNode)
            return
        }
        if (decoratorKeywordBare.matcher(decoratorsText).find()) {
            register(name, funcNode)
            return
        }

        val mRobotName = decoratorRobotNameAttr.matcher(decoratorsText)
        if (mRobotName.find()) {
            register(mRobotName.group(2), funcNode)
            return
        }

        if (autoKeywordsStack.peek() && isPublicFunctionName(name)) {
            register(name, funcNode)
        }
    }

    private fun isPublicFunctionName(name: CharSequence): Boolean {
        return !(name.startsWith("_") && !(name.startsWith("__") && name.endsWith("__")))
    }

    private fun extractFunctionName(funcNode: LighterASTNode): CharSequence? {
        val idNode = breadthFirst(funcNode) { it.tokenType == PyTokenTypes.IDENTIFIER }
        return idNode?.let { slice(it) }?.takeWhile { it.isJavaIdentifierPart() }?.takeIf { it.isNotEmpty() }
    }

    private fun register(keywordName: CharSequence, node: LighterASTNode) {
        val normalized = KeywordNameUtil.normalizeKeywordName(keywordName.toString())
        mutableKeywordPythonFunctionOccurrences.getOrPut(normalized) { mutableListOf() }.add(node.startOffset)
    }

    private fun findDecoratorListChild(parent: LighterASTNode): LighterASTNode? =
        ast.getChildren(parent).firstOrNull { it.tokenType == PyElementTypes.DECORATOR_LIST }

    private fun breadthFirst(root: LighterASTNode, predicate: (LighterASTNode) -> Boolean): LighterASTNode? {
        val queue = ArrayDeque<LighterASTNode>()
        queue.add(root)
        while (queue.isNotEmpty()) {
            val n = queue.removeFirst()
            if (predicate(n)) return n
            ast.getChildren(n).forEach { queue.add(it) }
        }
        return null
    }

    private fun slice(node: LighterASTNode): CharSequence = fileText.subSequence(node.startOffset, node.endOffset)
}
