package com.github.jnhyperion.hyperrobotframeworkplugin.ide.debugger.dap;

import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.jetbrains.rd.util.lifetime.Lifetime;
import com.jetbrains.rd.util.reactive.Signal;
import kotlinx.coroutines.TimeoutCancellationException;
import org.eclipse.lsp4j.debug.Capabilities;
import org.eclipse.lsp4j.debug.ConfigurationDoneArguments;
import org.eclipse.lsp4j.debug.InitializeRequestArguments;
import org.eclipse.lsp4j.debug.launch.DSPLauncher;
import org.eclipse.lsp4j.debug.services.IDebugProtocolServer;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.intellij.openapi.progress.util.ProgressIndicatorUtils.withTimeout;

public class RobotDebugAdapterProtocolCommunicator implements ProcessListener {

    private final int robotDebugPort;

    private final RobotDebugAdapterProtocolClient robotDebugClient;

    private final Signal<Void> afterInitialize = new Signal<>();

    private Socket socket;
    private IDebugProtocolServer robotDebugServer;

    private boolean initialized;

    public RobotDebugAdapterProtocolCommunicator(int robotDebugPort) {
        this.robotDebugPort = robotDebugPort;
        this.robotDebugClient = new RobotDebugAdapterProtocolClient();
    }

    @Override
    public void startNotified(@NotNull ProcessEvent event) {
        try {
            socket = tryConnectToServerWithTimeout(robotDebugPort);
            if (socket == null) {
                throw new RuntimeException("Failed to connect to debug server");
            }
            Launcher<IDebugProtocolServer> clientLauncher = DSPLauncher.createClientLauncher(robotDebugClient,
                                                                                             socket.getInputStream(),
                                                                                             socket.getOutputStream());
            clientLauncher.startListening();

            robotDebugServer = clientLauncher.getRemoteProxy();

            InitializeRequestArguments arguments = new InitializeRequestArguments();
            arguments.setClientID(UUID.randomUUID().toString());
            arguments.setAdapterID(UUID.randomUUID().toString());
            arguments.setClientName("HyperRobotFramework");
            arguments.setLocale("en_US");
            arguments.setLinesStartAt1(true);
            arguments.setColumnsStartAt1(true);
            arguments.setSupportsVariableType(true);
            arguments.setSupportsVariablePaging(false);
            arguments.setPathFormat("path");
            arguments.setSupportsRunInTerminalRequest(false);
            arguments.setSupportsStartDebuggingRequest(false);

            Capabilities initializeResponse = robotDebugServer.initialize(arguments).get();
            initialized = true;
            afterInitialize.fire(null);

            if (initializeResponse.getSupportsConfigurationDoneRequest()) {
                robotDebugServer.configurationDone(new ConfigurationDoneArguments()).get();
            }

            robotDebugServer.attach(Map.of()).get();

            robotDebugClient.getOnRobotExited().advise(Lifetime.Companion.getEternal(), args -> {
                processTerminated(new ProcessEvent(event.getProcessHandler(), args.exitCode()));
                return null;
            });
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processWillTerminate(@NotNull ProcessEvent event, boolean willBeDestroyed) {
        processTerminated(event);
    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {
        if (socket != null) {
            try {
                socket.close();
                socket = null;
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
            return withTimeout(10000L, () -> {
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
        } catch (TimeoutCancellationException ignored) {
        }
        return null;
    }
}
