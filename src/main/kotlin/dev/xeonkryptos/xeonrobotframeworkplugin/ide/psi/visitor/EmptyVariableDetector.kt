package dev.xeonkryptos.xeonrobotframeworkplugin.ide.psi.visitor

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotPythonExpression
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariable
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVariableContent
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.RobotVisitor

class EmptyVariableDetector : RobotVisitor() {

    var emptyVariable = true

    override fun visitVariable(o: RobotVariable) {
        super.visitVariable(o)
        if (o.variableName == null) {
            o.acceptChildren(this)
        } else {
            emptyVariable = false
        }
    }

    override fun visitVariableContent(o: RobotVariableContent) {
        super.visitVariableContent(o)
        emptyVariable = o.variableBodyIdList.isEmpty() && o.variableList.isEmpty()
    }

    override fun visitPythonExpression(o: RobotPythonExpression) {
        super.visitPythonExpression(o)
        emptyVariable = false
    }
}
