package dev.xeonkryptos.xeonrobotframeworkplugin.debugger.dap.model;

public record RobotExitedEventArguments(String reportFile, String logFile, String outputFile, Integer exitCode) {}
