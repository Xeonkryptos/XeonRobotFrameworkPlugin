package dev.xeonkryptos.xeonrobotframeworkplugin.ide.completion;

import com.intellij.openapi.util.Key;

public final class CompletionKeys {

    public static final Key<RobotLookupContext> ROBOT_LOOKUP_CONTEXT = Key.create("ROBOT_LOOKUP_CONTEXT");
    public static final Key<RobotLookupElementType> ROBOT_LOOKUP_ELEMENT_TYPE = Key.create("ROBOT_LOOKUP_ELEMENT_TYPE");
    public static final Key<RobotLookupScope> ROBOT_LOOKUP_SCOPE = Key.create("ROBOT_LOOKUP_SCOPE");
    public static final Key<Boolean> ROBOT_REQUIRED_VALUE = Key.create("ROBOT_REQUIRED_VALUE");

    private CompletionKeys() {
        throw new UnsupportedOperationException("Utility class");
    }
}
