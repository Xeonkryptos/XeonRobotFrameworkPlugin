package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import com.intellij.util.IncorrectOperationException
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionId
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator

class RobotVariableExpressionIdManipulator : AbstractElementManipulator<RobotVariableExpressionId>() {

    @Throws(IncorrectOperationException::class)
    override fun handleContentChange(variableExpressionId: RobotVariableExpressionId, textRange: TextRange, newText: String): RobotVariableExpressionId? {
        val newContent = textRange.replace(variableExpressionId.text, newText)

        val newId = RobotElementGenerator.getInstance(variableExpressionId.project).createNewVariableExpressionId(newContent) ?: return null
        return variableExpressionId.replace(newId) as RobotVariableExpressionId?
    }
}
