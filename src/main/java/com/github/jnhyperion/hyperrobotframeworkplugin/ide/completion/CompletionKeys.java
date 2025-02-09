package com.github.jnhyperion.hyperrobotframeworkplugin.ide.completion;

import com.intellij.openapi.util.Key;

final class CompletionKeys {

    static final Key<RobotLookupContext> ROBOT_LOOKUP_CONTEXT = Key.create("ROBOT_LOOKUP_CONTEXT");
    static final Key<RobotLookupElementType> ROBOT_LOOKUP_ELEMENT_TYPE = Key.create("ROBOT_LOOKUP_ELEMENT_TYPE");

    private CompletionKeys() {
        throw new UnsupportedOperationException("Utility class");
    }
}
