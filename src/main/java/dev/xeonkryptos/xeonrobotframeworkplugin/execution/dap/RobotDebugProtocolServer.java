package dev.xeonkryptos.xeonrobotframeworkplugin.execution.dap;

import org.eclipse.lsp4j.debug.services.IDebugProtocolServer;
import org.eclipse.lsp4j.jsonrpc.services.JsonRequest;

import java.util.concurrent.CompletableFuture;

public interface RobotDebugProtocolServer extends IDebugProtocolServer, RobotDebugProcolSynchronizer {

    @Override
    @JsonRequest("robot/sync")
    CompletableFuture<Void> robotSync();
}
