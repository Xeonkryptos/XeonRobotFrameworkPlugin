package dev.xeonkryptos.xeonrobotframeworkplugin.lsp

import com.intellij.openapi.vfs.VirtualFile

object RobotLspLexerFileContext {
    val currentFile: ThreadLocal<VirtualFile?> = ThreadLocal()
}
