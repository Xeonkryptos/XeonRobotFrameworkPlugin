package dev.xeonkryptos.xeonrobotframeworkplugin.util

import com.intellij.util.net.NetUtils
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import java.io.IOException
import java.net.ServerSocket

object NetworkUtil {

    @JvmStatic
    @JvmOverloads
    fun findAvailableSocketPort(port: Int = 0): Int {
        try {
            ServerSocket(port).use { serverSocket ->
                // workaround for linux : calling close() immediately after opening socket
                // may result that socket is not closed
                synchronized(serverSocket) {
                    try {
                        @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN") (serverSocket as Object).wait(1)
                    } catch (_: InterruptedException) {
                    }
                }
                return serverSocket.getLocalPort()
            }
        } catch (_: Exception) {
        }
        try {
            return NetUtils.findAvailableSocketPort()
        } catch (e: IOException) {
            throw RuntimeException(RobotBundle.message("runcfg.error.message.failed.to.find.free.socket.port"), e)
        }
    }
}
