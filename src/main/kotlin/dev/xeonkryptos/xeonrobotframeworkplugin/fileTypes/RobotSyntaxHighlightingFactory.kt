package dev.xeonkryptos.xeonrobotframeworkplugin.fileTypes

import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class RobotSyntaxHighlightingFactory : SyntaxHighlighterFactory() {

    override fun getSyntaxHighlighter(project: Project?, file: VirtualFile?): SyntaxHighlighter {
        return RobotHighlighter(project)
    }
}
