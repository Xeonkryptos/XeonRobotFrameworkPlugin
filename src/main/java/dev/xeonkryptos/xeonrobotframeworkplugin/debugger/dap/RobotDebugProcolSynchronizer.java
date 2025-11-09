package dev.xeonkryptos.xeonrobotframeworkplugin.debugger.dap;

import java.util.concurrent.CompletableFuture;

interface RobotDebugProcolSynchronizer {

    CompletableFuture<Void> robotSync();
}
