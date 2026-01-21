package dev.xeonkryptos.xeonrobotframeworkplugin.util

import com.intellij.util.net.NetUtils
import dev.xeonkryptos.xeonrobotframeworkplugin.RobotBundle
import java.io.IOException
import java.net.ServerSocket

object NetworkUtil {

    @JvmStatic
    fun findAvailableSocketPort(port: Int): Int {
        try {
            ServerSocket(port).use { serverSocket ->
                // workaround for linux : calling close() immediately after opening socket
                // may result that socket is not closed
                synchronized(serverSocket) {
                    try {
                        (serverSocket as Object).wait(1)
                    } catch (ignored: InterruptedException) {
                    }
                }
                return serverSocket.getLocalPort()
            }
        } catch (ignored: Exception) {
        }
        try {
            return NetUtils.findAvailableSocketPort()
        } catch (e: IOException) {
            throw RuntimeException(RobotBundle.message("runcfg.error.message.failed.to.find.free.socket.port"), e)
        }
    }
}
