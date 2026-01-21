package dev.xeonkryptos.xeonrobotframeworkplugin.debugger

import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.ArrayUtil
import com.intellij.xdebugger.XDebugSession
import com.intellij.xdebugger.XSourcePosition
import com.intellij.xdebugger.breakpoints.XBreakpoint
import com.intellij.xdebugger.breakpoints.XBreakpointHandler
import com.intellij.xdebugger.breakpoints.XLineBreakpoint
import com.intellij.xdebugger.frame.XSuspendContext
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.threading.coroutines.adviseSuspend
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.dap.RobotDebugAdapterProtocolCommunicator
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.debugger.RobotSuspendContext
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.debugger.breakpoint.RobotExceptionBreakpointHandler
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.debugger.breakpoint.RobotExceptionBreakpointProperties
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.debugger.breakpoint.RobotLineBreakpointHandler
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.debugger.breakpoint.RobotLineBreakpointProperties
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.eclipse.lsp4j.debug.ContinueArguments
import org.eclipse.lsp4j.debug.NextArguments
import org.eclipse.lsp4j.debug.PauseArguments
import org.eclipse.lsp4j.debug.SetBreakpointsArguments
import org.eclipse.lsp4j.debug.Source
import org.eclipse.lsp4j.debug.SourceBreakpoint
import org.eclipse.lsp4j.debug.StackTraceArguments
import org.eclipse.lsp4j.debug.StepInArguments
import org.eclipse.lsp4j.debug.StepOutArguments
import org.eclipse.lsp4j.debug.StoppedEventArguments
import org.eclipse.lsp4j.debug.TerminateArguments
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.Volatile
import kotlin.concurrent.withLock

class RobotDebugProcess(private val session: XDebugSession, private val robotDAPCommunicator: RobotDebugAdapterProtocolCommunicator) {
    private val exceptionBreakpoints = mutableListOf<ExceptionBreakpointInfo>()

    private val breakpoints = CopyOnWriteArrayList<BreakPointInfo>()
    private val breakpointMap = mutableMapOf<VirtualFile, MutableMap<Int, BreakPointInfo>>()
    private val breakpointsMapMutex = ReentrantLock()

    private val breakpointHandler = RobotLineBreakpointHandler(this)
    private val exceptionBreakpointHandler = RobotExceptionBreakpointHandler(this)

    private var oneTimeBreakpointInfo: OneTimeBreakpointInfo? = null

    private val debugClient
        get() = robotDAPCommunicator.debugClient
    private val debugServer
        get() = robotDAPCommunicator.debugServer

    val breakpointHandlers: Array<XBreakpointHandler<*>>
        get() = arrayOf(breakpointHandler, exceptionBreakpointHandler)

    init {
        session.setPauseActionSupported(true)

        robotDAPCommunicator.afterInitialize.adviseSuspend(Lifetime.Eternal, Dispatchers.IO) { sendBreakpointRequest() }
        debugClient.onStopped.adviseSuspend(Lifetime.Eternal, Dispatchers.IO, this::handleOnStopped)
    }

    private suspend fun createRobotCodeSuspendContext(threadId: Int): RobotSuspendContext {
        val debugServer = debugServer
        val stackTraceArguments = StackTraceArguments().apply { this.threadId = threadId }
        val stackTraceResponse = debugServer.stackTrace(stackTraceArguments).await()
        return RobotSuspendContext(stackTraceResponse, threadId, debugServer, session)
    }

    private suspend fun handleOnStopped(args: StoppedEventArguments) {
        val robotCodeSuspendContext = createRobotCodeSuspendContext(args.threadId)
        if (session.areBreakpointsMuted()) {
            val continueArguments = ContinueArguments().apply { threadId = args.threadId }
            debugServer.continue_(continueArguments).await()
            return
        }
        when (args.reason) {
            "breakpoint" -> {
                val bp = breakpoints.asSequence().filter { it.id != null && ArrayUtil.contains(it.id, *args.hitBreakpointIds) }.firstOrNull()
                if (bp is LineBreakpointInfo) {
                    if (!session.breakpointReached(bp.breakpoint, null, robotCodeSuspendContext)) {
                        val continueArguments = ContinueArguments().apply { threadId = args.threadId }
                        debugServer.continue_(continueArguments).await()
                    }
                } else {
                    session.positionReached(robotCodeSuspendContext)
                }
            }

            "exception" -> {
                var exceptionBreakpointInfo: ExceptionBreakpointInfo? = null
                breakpointsMapMutex.withLock {
                    if (!exceptionBreakpoints.isEmpty()) {
                        exceptionBreakpointInfo = exceptionBreakpoints.first()
                    }
                }
                if (exceptionBreakpointInfo == null || !session.breakpointReached(exceptionBreakpointInfo.breakpoint, null, robotCodeSuspendContext)) {
                    val continueArguments = ContinueArguments()
                    continueArguments.threadId = args.threadId
                    debugServer.continue_(continueArguments).await()
                }
            }

            else -> session.positionReached(robotCodeSuspendContext)
        }
        removeCurrentOneTimeBreakpoint()
    }

    fun registerExceptionBreakpoint(breakpoint: XBreakpoint<RobotExceptionBreakpointProperties>) {
        breakpointsMapMutex.withLock { exceptionBreakpoints.add(ExceptionBreakpointInfo(null, breakpoint)) }
    }

    fun unregisterExceptionBreakpoint(breakpoint: XBreakpoint<RobotExceptionBreakpointProperties>) {
        breakpointsMapMutex.withLock { exceptionBreakpoints.removeIf { it.breakpoint == breakpoint } }
    }

    fun registerBreakpoint(breakpoint: XLineBreakpoint<RobotLineBreakpointProperties>) {
        runBlocking {
            var file: VirtualFile? = null
            breakpointsMapMutex.withLock {
                breakpoint.sourcePosition?.let {
                    val lineBreakpointInfo = LineBreakpointInfo(null, breakpoint)
                    breakpoints.add(lineBreakpointInfo)

                    val bpMap = breakpointMap.computeIfAbsent(it.file) { mutableMapOf() }
                    bpMap[breakpoint.line] = lineBreakpointInfo

                    file = it.file
                }
            }
            file?.let { sendBreakpointRequest(it) }
        }
    }

    fun unregisterBreakpoint(breakpoint: XLineBreakpoint<RobotLineBreakpointProperties?>) {
        runBlocking {
            var file: VirtualFile? = null
            breakpointsMapMutex.withLock {
                breakpoint.sourcePosition?.let {
                    if (breakpointMap.containsKey(it.file)) {
                        breakpoints.removeIf { bpi -> bpi is LineBreakpointInfo && bpi.breakpoint == breakpoint }

                        val bpMap: MutableMap<Int, BreakPointInfo>? = breakpointMap[it.file]
                        bpMap?.remove(breakpoint.line)

                        file = it.file
                    }
                }
            }
            file?.let { sendBreakpointRequest(it) }
        }
    }

    private suspend fun sendBreakpointRequest() {
        if (robotDAPCommunicator.isInitialized) {
            for (file in breakpointMap.keys) {
                sendBreakpointRequest(file)
            }
        }
    }

    private suspend fun sendBreakpointRequest(file: VirtualFile) {
        if (!robotDAPCommunicator.isInitialized) return

        val breakpoints = breakpointMap[file]?.values
        if (breakpoints == null || breakpoints.isEmpty()) return

        val arguments = SetBreakpointsArguments()
        val source = Source()
        source.path = file.toNioPath().toString()
        arguments.source = source

        val dapBreakpoints = breakpoints.map { bp ->
            val sourceBreakpoint = SourceBreakpoint()
            sourceBreakpoint.line = bp.line + 1
            if (bp is LineBreakpointInfo) {
                val conditionExpression = bp.breakpoint.conditionExpression
                val logExpressionObject = bp.breakpoint.logExpressionObject

                val condition = conditionExpression?.expression
                val logMessage = logExpressionObject?.expression
                sourceBreakpoint.condition = condition
                sourceBreakpoint.logMessage = logMessage
            }
            sourceBreakpoint
        }

        arguments.breakpoints = dapBreakpoints.toTypedArray()

        val response = debugServer.setBreakpoints(arguments).await()

        breakpoints.forEach { breakpoint ->
            response.breakpoints.firstOrNull { it.line - 1 == breakpoint.line }?.let { responseBreakpoint ->
                breakpoint.id = responseBreakpoint.id
                val lineBreakpointInfo = breakpoint as LineBreakpointInfo
                if (responseBreakpoint.isVerified) {
                    session.setBreakpointVerified(lineBreakpointInfo.breakpoint)
                } else {
                    session.setBreakpointInvalid(lineBreakpointInfo.breakpoint, "Invalid breakpoint")
                }
            }
        }
    }

    private suspend fun removeCurrentOneTimeBreakpoint() {
        if (oneTimeBreakpointInfo != null) {
            val bpMap = breakpointMap[oneTimeBreakpointInfo!!.file]
            if (bpMap != null) {
                bpMap.remove(oneTimeBreakpointInfo!!.line)
                sendBreakpointRequest(oneTimeBreakpointInfo!!.file)
            }
            oneTimeBreakpointInfo = null
        }
    }

    fun startStepOver(context: XSuspendContext?) {
        runBlocking {
            if (context is RobotSuspendContext) {
                val nextArguments = NextArguments().apply { threadId = context.threadId }
                debugServer.next(nextArguments).await()
            }
        }
    }

    fun startStepInto(context: XSuspendContext?) {
        runBlocking {
            if (context is RobotSuspendContext) {
                val stepInArguments = StepInArguments().apply { threadId = context.threadId }
                debugServer.stepIn(stepInArguments).await()
            }
        }
    }

    fun startStepOut(context: XSuspendContext?) {
        runBlocking {
            if (context is RobotSuspendContext) {
                val stepOutArguments = StepOutArguments()
                stepOutArguments.threadId = context.threadId
                debugServer.stepOut(stepOutArguments).await()
            }
        }
    }

    fun runToPosition(position: XSourcePosition, context: XSuspendContext?) {
        runBlocking {
            if (!breakpointMap.containsKey(position.file)) {
                breakpointMap[position.file] = mutableMapOf()
            }
            val bpMap = breakpointMap[position.file]
            removeCurrentOneTimeBreakpoint()
            if (bpMap == null || bpMap.containsKey(position.line)) {
                return@runBlocking
            }

            val currentOneTimeBreakpointInfo = OneTimeBreakpointInfo(null, position)
            oneTimeBreakpointInfo = currentOneTimeBreakpointInfo
            bpMap[position.line] = currentOneTimeBreakpointInfo

            sendBreakpointRequest(position.file)
            resume(context)
        }
    }

    fun startPausing() {
        runBlocking {
            val pauseArguments = PauseArguments().apply { threadId = 0 }
            debugServer.pause(pauseArguments).await()
        }
    }

    fun resume(context: XSuspendContext?) {
        runBlocking {
            if (context is RobotSuspendContext) {
                val continueArguments = ContinueArguments().apply { threadId = context.threadId }
                debugServer.continue_(continueArguments).await()
            }
        }
    }

    fun stop() {
        runBlocking {
            try {
                val terminateArguments = TerminateArguments().apply { restart = false }
                debugServer.terminate(terminateArguments).await()
            } catch (_: Exception) {
                // Ignore may be the server is already terminated
            }
        }
    }

    private open class BreakPointInfo(@field:Volatile var id: Int?, val line: Int, val file: VirtualFile)

    private class LineBreakpointInfo(id: Int?, val breakpoint: XLineBreakpoint<RobotLineBreakpointProperties>) : BreakPointInfo(
        id, breakpoint.line, (if (breakpoint.sourcePosition != null) breakpoint.sourcePosition!!.file else null)!!
    )

    @JvmRecord
    private data class ExceptionBreakpointInfo(val id: Int?, val breakpoint: XBreakpoint<RobotExceptionBreakpointProperties>)

    private class OneTimeBreakpointInfo(id: Int?, position: XSourcePosition) : BreakPointInfo(id, position.line, position.file)
}
