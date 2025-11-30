package dev.xeonkryptos.xeonrobotframeworkplugin.execution.dap;

import java.util.concurrent.CompletableFuture;

interface RobotDebugProcolSynchronizer {

    CompletableFuture<Void> robotSync();
}
