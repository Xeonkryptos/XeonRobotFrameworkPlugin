package dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.manipulator

import com.intellij.openapi.util.TextRange
import com.intellij.psi.AbstractElementManipulator
import com.intellij.util.IncorrectOperationException
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableDatatype
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.util.RobotElementGenerator

class RobotVariableDatatypeManipulator : AbstractElementManipulator<RobotVariableDatatype>() {

    @Throws(IncorrectOperationException::class)
    override fun handleContentChange(variableDatatype: RobotVariableDatatype, textRange: TextRange, newText: String): RobotVariableDatatype? {
        val original = variableDatatype.text
        val newContent = textRange.replace(original, newText)

        val newVariableDatatype = RobotElementGenerator.getInstance(variableDatatype.project).createNewVariableDatatype(newContent) ?: return null
        return variableDatatype.replace(newVariableDatatype) as RobotVariableDatatype?
    }
}
