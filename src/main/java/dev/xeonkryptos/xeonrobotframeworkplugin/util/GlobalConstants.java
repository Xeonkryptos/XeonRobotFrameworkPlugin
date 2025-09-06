package dev.xeonkryptos.xeonrobotframeworkplugin.util;

@SuppressWarnings("ApplicationServiceAsStaticFinalFieldOrProperty")
public final class GlobalConstants {

    public static final String ELLIPSIS = "...";

    public static final String DEFAULT_INDENTATION = "    ";

    public static final String ROBOT_BUILT_IN = "robot.libraries.BuiltIn";

    public static final String DEPRECATED_PREFIX = "*DEPRECATED";

    private GlobalConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
