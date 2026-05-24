package dev.xeonkryptos.xeonrobotframeworkplugin.lexer

import com.intellij.openapi.project.Project
import dev.xeonkryptos.xeonrobotframeworkplugin.psi.RobotLexer

class RobotLexerExtension(project: Project? = null) : RobotLexer(project) {

    override fun reset(buffer: CharSequence?, start: Int, end: Int, initialState: Int) {
        super.reset(buffer, start, end, initialState)
        resetLexer()
    }
}
