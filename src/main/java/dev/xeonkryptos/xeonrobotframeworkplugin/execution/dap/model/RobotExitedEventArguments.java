package dev.xeonkryptos.xeonrobotframeworkplugin.execution.dap.model;

public record RobotExitedEventArguments(String reportFile, String logFile, String outputFile, Integer exitCode) {}
