package dev.xeonkryptos.xeonrobotframeworkplugin.completion;

public enum RobotLookupContext {

    /**
     * Listing of keywords, usually in keyword definition or wherever you're able to place keywords
     */
    KEYWORDS, WITHIN_KEYWORD_STATEMENT, IMPORT,

    /**
     * Members (attributes, properties and methods) of an object accessed with the extended variable syntax, e.g. {@code ${OBJECT.<here>}}
     */
    EXTENDED_VARIABLE_SYNTAX
}
