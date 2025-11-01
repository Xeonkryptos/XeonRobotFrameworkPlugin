package dev.xeonkryptos.xeonrobotframeworkplugin.debugger.dap;

import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.jetbrains.rd.util.reactive.Signal;
import dev.xeonkryptos.xeonrobotframeworkplugin.MyLogger;
import kotlinx.coroutines.TimeoutCancellationException;
import org.eclipse.lsp4j.debug.Capabilities;
import org.eclipse.lsp4j.debug.ConfigurationDoneArguments;
import org.eclipse.lsp4j.debug.InitializeRequestArguments;
import org.eclipse.lsp4j.debug.launch.DSPLauncher;
import org.eclipse.lsp4j.debug.services.IDebugProtocolServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.intellij.openapi.progress.util.ProgressIndicatorUtils.withTimeout;

public class RobotDebugAdapterProtocolCommunicator implements ProcessListener {

    private final int robotDebugPort;

    private final RobotDebugAdapterProtocolClient robotDebugClient;

    private final Signal<Void> afterInitialize = new Signal<>();

    private final AtomicBoolean initializing = new AtomicBoolean(false);

    private volatile Socket socket;
    private IDebugProtocolServer robotDebugServer;

    private volatile boolean initialized;

    public RobotDebugAdapterProtocolCommunicator(int robotDebugPort) {
        this.robotDebugPort = robotDebugPort;
        this.robotDebugClient = new RobotDebugAdapterProtocolClient();
    }

    @Override
    public void startNotified(@NotNull ProcessEvent event) {
        if (!initializing.getAndSet(true)) {
            ApplicationManager.getApplication().executeOnPooledThread(this::connect);
        }
    }

    private void connect() {
        Socket localSocket = tryConnectToServerWithTimeout(robotDebugPort);
        socket = localSocket;
        if (localSocket == null) {
            MyLogger.logger.error("Couldn't connect to Robot debug server at port %d".formatted(robotDebugPort));
            initializing.set(false);
        } else {
            try {
                Launcher<IDebugProtocolServer> clientLauncher = DSPLauncher.createClientLauncher(robotDebugClient,
                                                                                                 localSocket.getInputStream(),
                                                                                                 localSocket.getOutputStream());
                clientLauncher.startListening();

                robotDebugServer = clientLauncher.getRemoteProxy();

                InitializeRequestArguments arguments = new InitializeRequestArguments();
                arguments.setClientID(UUID.randomUUID().toString());
                arguments.setAdapterID(UUID.randomUUID().toString());
                arguments.setClientName("XeonkryptosRobotFramework");
                arguments.setLocale("en_US");
                arguments.setLinesStartAt1(true);
                arguments.setColumnsStartAt1(true);
                arguments.setSupportsVariableType(true);
                arguments.setSupportsVariablePaging(false);
                arguments.setPathFormat("path");
                arguments.setSupportsRunInTerminalRequest(false);
                arguments.setSupportsStartDebuggingRequest(false);

                Capabilities initializeResponse = robotDebugServer.initialize(arguments).get(5L, TimeUnit.SECONDS);
                initialized = true;
                afterInitialize.fire(null);

                if (initializeResponse.getSupportsConfigurationDoneRequest()) {
                    robotDebugServer.configurationDone(new ConfigurationDoneArguments()).get();
                }

                robotDebugServer.attach(Map.of()).get();

                MyLogger.logger.info("Connected to Robot debug server at port %d".formatted(robotDebugPort));
            } catch (Exception e) {
                MyLogger.logger.error("Failed to connect to Robot debug server at port %d".formatted(robotDebugPort), e);
            }
        }
    }

    @Override
    public void processWillTerminate(@NotNull ProcessEvent event, boolean willBeDestroyed) {
        processTerminated(event);
    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        event.getProcessHandler().removeProcessListener(this);
        Socket localSocket = socket;
        socket = null;
        if (localSocket != null) {
            try {
                localSocket.close();
            } catch (IOException ignore) {
            }
        }
    }

    public RobotDebugAdapterProtocolClient getDebugClient() {
        return robotDebugClient;
    }

    public IDebugProtocolServer getDebugServer() {
        return robotDebugServer;
    }

    public Signal<Void> getAfterInitialize() {
        return afterInitialize;
    }

    public boolean isInitialized() {
        return initialized;
    }

    private Socket tryConnectToServerWithTimeout(int port) {
        try {
            return withTimeout(10_000L, () -> {
                Socket socket = null;
                while (socket == null || !socket.isConnected()) {
                    socket = null;
                    try {
                        socket = new Socket("localhost", port);
                    } catch (Exception ignored) {
                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(100L);
                    } catch (InterruptedException e) {
                        throw new ProcessCanceledException(e);
                    }
                }
                return socket;
            });
        } catch (TimeoutCancellationException e) {
            MyLogger.logger.error("Couldn't connect to debug process in the expected time", e);
        }
        return null;
    }
}
