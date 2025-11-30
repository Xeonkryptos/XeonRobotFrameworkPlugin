package dev.xeonkryptos.xeonrobotframeworkplugin.execution.dap.model;

public record RobotLogMessageEventArguments(String itemId,
                                            String source,
                                            Integer lineno,
                                            Integer column,
                                            String message,
                                            String level,
                                            String timestamp,
                                            String html) {}
