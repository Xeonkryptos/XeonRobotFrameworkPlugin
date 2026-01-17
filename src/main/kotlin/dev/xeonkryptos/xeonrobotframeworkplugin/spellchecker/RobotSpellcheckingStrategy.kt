package dev.xeonkryptos.xeonrobotframeworkplugin.spellchecker

import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiNameIdentifierOwner
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.spellchecker.inspections.PlainTextSplitter
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy
import com.intellij.spellchecker.tokenizer.TokenConsumer
import com.intellij.spellchecker.tokenizer.Tokenizer
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotLiteralConstantValue
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPythonExpression
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableBodyId
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableContent
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableNestedAccessContent
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor.RecursiveRobotVisitor

class RobotSpellcheckingStrategy : SpellcheckingStrategy(), DumbAware {

    override fun getTokenizer(element: PsiElement?): Tokenizer<*> {
        if (element is PsiNameIdentifierOwner) return TEXT_TOKENIZER

        val visitor = RobotSpellcheckingVisitor()
        element?.accept(visitor)
        return visitor.tokenizer ?: super.getTokenizer(element)
    }

    private class RobotSpellcheckingVisitor : RobotVisitor() {

        var tokenizer: Tokenizer<*>? = null

        override fun visitVariable(o: RobotVariable) {
            val pythonExpression = PsiTreeUtil.findChildOfType(o, RobotPythonExpression::class.java)
            if (pythonExpression != null) {
                tokenizer = EMPTY_TOKENIZER
            } else {
                val variableContent = PsiTreeUtil.findChildOfType(o, RobotVariableContent::class.java)
                if (variableContent != null) tokenizer = RobotVariableContentTokenizer()
            }
        }
    }

    private class RobotVariableContentTokenizer : Tokenizer<RobotVariable>() {

        override fun tokenize(element: RobotVariable, consumer: TokenConsumer) {
            val visitor = RobotVariableTokenizerVisitor(consumer)
            element.accept(visitor)
        }
    }

    private class RobotVariableTokenizerVisitor(private val consumer: TokenConsumer) : RecursiveRobotVisitor() {

        override fun visitVariableBodyId(o: RobotVariableBodyId) {
            consumer.consumeToken(o, true, PlainTextSplitter.getInstance())
        }

        override fun visitLiteralConstantValue(o: RobotLiteralConstantValue) {
            if (o.parent is RobotVariableNestedAccessContent) {
                consumer.consumeToken(o, PlainTextSplitter.getInstance())
            }
        }
    }
}
