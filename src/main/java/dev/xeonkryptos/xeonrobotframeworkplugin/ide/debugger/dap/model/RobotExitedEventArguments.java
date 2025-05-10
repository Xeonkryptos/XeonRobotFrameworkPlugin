package dev.xeonkryptos.xeonrobotframeworkplugin.ide.debugger.dap.model;

public record RobotExitedEventArguments(String reportFile, String logFile, String outputFile, Integer exitCode) {}
