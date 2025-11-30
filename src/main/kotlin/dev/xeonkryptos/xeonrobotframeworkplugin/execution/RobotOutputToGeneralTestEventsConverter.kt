package dev.xeonkryptos.xeonrobotframeworkplugin.execution

import com.intellij.execution.testframework.sm.ServiceMessageBuilder
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.util.Key
import com.intellij.util.Urls.newLocalFileUrl
import com.intellij.util.Urls.newUrl
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.reactive.adviseEternal
import dev.xeonkryptos.xeonrobotframeworkplugin.config.RobotOptionsProvider
import dev.xeonkryptos.xeonrobotframeworkplugin.execution.dap.model.RobotExecutionEventArguments
import jetbrains.buildServer.messages.serviceMessages.ServiceMessage
import jetbrains.buildServer.messages.serviceMessages.ServiceMessageVisitor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout

class RobotOutputToGeneralTestEventsConverter(testFrameworkName: String, consoleProperties: RobotConsoleProperties) : OutputToGeneralTestEventsConverter(
    testFrameworkName, consoleProperties
) {

    private var _firstCall = false
    private lateinit var visitor: ServiceMessageVisitor
    private val testItemIdStack = mutableListOf<String>()

    private val testsOnlyMode = RobotOptionsProvider.getInstance(consoleProperties.project).testsOnlyMode()

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    private val myContext = newSingleThreadContext("RobotOutputToGeneralTestEventsConverter")

    private var configurationDone = CompletableDeferred<Unit>()

    init {
        val dapCommunicator = consoleProperties.state?.dapCommunicator
        dapCommunicator?.afterInitialize?.adviseEternal {
            runBlocking {
                withTimeout(5000) { configurationDone.await() }
            }
        }

        val debugClient = dapCommunicator?.debugClient
        debugClient?.onRobotStarted?.advise(Lifetime.Eternal, this::robotStarted)
        debugClient?.onRobotEnded?.advise(Lifetime.Eternal, this::robotEnded)
        /*debugClient?.onOutput?.advise(Lifetime.Eternal) { args ->
            val msg = if (args.category == OutputEventArgumentsCategory.STDERR) ServiceMessageBuilder.testStdErr(args.category)
            else ServiceMessageBuilder.testStdOut(args.category)

            msg.addAttribute("nodeId", testItemIdStack.lastOrNull() ?: "0")
            msg.addAttribute("out", "\u001b[38;5;243m${args.output}\u001b[0m")

            processServiceMessageFromRobot(msg)
        }*/
    }

    private fun robotStarted(args: RobotExecutionEventArguments) {
        testItemIdStack.add(args.id)

        val msg = when (args.type) {
            "suite" -> ServiceMessageBuilder.testSuiteStarted(args.name)
            "test" -> if (testsOnlyMode) ServiceMessageBuilder.testStarted(args.name) else ServiceMessageBuilder.testSuiteStarted(args.name)
            "keyword" -> if (!testsOnlyMode) ServiceMessageBuilder.testStarted(args.name) else null

            else -> null
        }

        processRobotMessage(msg, args)
    }

    private fun robotEnded(args: RobotExecutionEventArguments) {
        val msg = when (args.type) {
            "suite" -> ServiceMessageBuilder.testSuiteFinished(args.name)
            "test" -> if (testsOnlyMode) onFinishedTestItem(args) else ServiceMessageBuilder.testSuiteFinished(args.name)
            "keyword" -> if (!testsOnlyMode) onFinishedTestItem(args) else null

            else -> null
        }

        processRobotMessage(msg, args)

        val lastId = testItemIdStack.removeLast()
        if (lastId != args.id) {
            thisLogger().warn("Test item ID stack is out of sync. Expected $lastId, got ${args.id}")
        }
    }

    private fun onFinishedTestItem(args: RobotExecutionEventArguments) = when (args.attributes.status) {
        "PASS" -> ServiceMessageBuilder.testFinished(args.name).apply {
            if (args.attributes.message != null) {
                addAttribute("message", args.attributes.message)
            }
        }

        "SKIP" -> ServiceMessageBuilder.testIgnored(args.name).apply {
            addAttribute("message", args.attributes.message ?: "Skipped")
        }

        else -> ServiceMessageBuilder.testFailed(args.name).apply {
            addAttribute("message", args.attributes.message ?: "Error")
        }
    }

    private fun processRobotMessage(msg: ServiceMessageBuilder?, args: RobotExecutionEventArguments) {
        if (msg != null) {
            with(msg) {
                addAttribute("nodeId", args.id)
                addAttribute("parentNodeId", args.parentId ?: "0")
                if (args.attributes.source != null) {
                    val localFileUrl = newLocalFileUrl(args.attributes.source!!).toString()
                    val uri = newUrl("robotcode", "/", localFileUrl).addParameters(mapOf("line" to ((args.attributes.lineno ?: 1) - 1).toString()))

                    addAttribute("locationHint", uri.toString())
                }
                addAttribute("duration", (args.attributes.elapsedtime ?: 0).toString()).toString()
            }
            this.processServiceMessageFromRobot(msg)
        }
    }

    private fun processServiceMessageFromRobot(msg: ServiceMessageBuilder) {
        ServiceMessage.parse(msg.toString())?.let { processServiceMessage(it, visitor) }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun processServiceMessages(text: String, outputType: Key<*>, visitor: ServiceMessageVisitor): Boolean {
        if (!_firstCall) {
            _firstCall = true
            this.visitor = visitor

            configurationDone.complete(Unit)
        }
        runBlocking(myContext) { fireOnUncapturedOutput(text, outputType) }
        return true
    }
}
