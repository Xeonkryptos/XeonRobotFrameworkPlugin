package dev.xeonkryptos.xeonrobotframeworkplugin.psi.visitor

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotTypes
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPythonExpression
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableContent
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor

class EmptyVariableDetector : RobotVisitor() {

    var emptyVariable = true

    override fun visitVariable(o: RobotVariable) {
        if (o.variableName == null) {
            o.acceptChildren(this)
        } else {
            emptyVariable = false
        }
    }

    override fun visitVariableContent(o: RobotVariableContent) {
        emptyVariable = o.node.findChildByType(RobotTypes.VARIABLE_BODY) == null && o.variableList.isEmpty()
    }

    override fun visitPythonExpression(o: RobotPythonExpression) {
        emptyVariable = false
    }
}
