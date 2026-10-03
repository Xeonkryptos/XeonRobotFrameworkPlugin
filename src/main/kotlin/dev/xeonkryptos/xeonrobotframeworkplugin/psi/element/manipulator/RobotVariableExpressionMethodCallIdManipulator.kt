package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import com.intellij.util.IncorrectOperationException
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableExpressionMethodCallId
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator

class RobotVariableExpressionMethodCallIdManipulator : AbstractElementManipulator<RobotVariableExpressionMethodCallId>() {

    @Throws(IncorrectOperationException::class)
    override fun handleContentChange(methodCallId: RobotVariableExpressionMethodCallId, textRange: TextRange, newText: String): RobotVariableExpressionMethodCallId? {
        val newContent = textRange.replace(methodCallId.text, newText)

        val newId = RobotElementGenerator.getInstance(methodCallId.project).createNewVariableExpressionMethodCallId(newContent) ?: return null
        return methodCallId.replace(newId) as RobotVariableExpressionMethodCallId?
    }
}
