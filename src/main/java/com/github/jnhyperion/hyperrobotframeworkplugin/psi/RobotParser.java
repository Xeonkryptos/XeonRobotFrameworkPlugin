package com.github.jnhyperion.hyperrobotframeworkplugin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotParser implements PsiParser {

    @NotNull
    @Override
    public ASTNode parse(@NotNull IElementType type, @NotNull PsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        while (!builder.eof()) {
            IElementType tokenType = builder.getTokenType();
            if (RobotTokenTypes.HEADING == tokenType) {
                parseHeading(builder);
            } else {
                builder.advanceLexer();
            }
        }
        marker.done(type);
        return builder.getTreeBuilt();
    }

    private static void parseHeading(@NotNull PsiBuilder builder) {
        IElementType iElementType = builder.getTokenType();
        if (RobotTokenTypes.HEADING == iElementType) {
            PsiBuilder.Marker headingMarker = null;
            while (!builder.eof()) {
                IElementType type = builder.getTokenType();
                if (RobotTokenTypes.HEADING == type) {
                    done(headingMarker, RobotTokenTypes.HEADING);
                    headingMarker = builder.mark();
                    builder.advanceLexer();
                } else {
                    type = builder.getTokenType();
                    if (RobotTokenTypes.HEADING != type) {
                        if (RobotTokenTypes.SETTING == type) {
                            parseSetting(builder);
                        } else if (RobotTokenTypes.VARIABLE_DEFINITION == type && isNextToken(builder, RobotTokenTypes.WHITESPACE)) {
                            parseWithArguments(builder, RobotTokenTypes.VARIABLE_DEFINITION);
                        } else if (RobotTokenTypes.BRACKET_SETTING == type) {
                            parseWithArguments(builder, RobotTokenTypes.BRACKET_SETTING);
                        } else if (RobotTokenTypes.KEYWORD_DEFINITION == type || (RobotTokenTypes.VARIABLE_DEFINITION == type && isNextToken(builder,
                                                                                                                                             RobotTokenTypes.KEYWORD_DEFINITION))) {
                            parseKeywordDefinition(builder);
                        } else if (RobotTokenTypes.KEYWORD == type) {
                            parseKeywordStatement(builder, RobotTokenTypes.KEYWORD_STATEMENT, false);
                        } else if (RobotTokenTypes.IMPORT == type) {
                            parseWithArguments(builder, RobotTokenTypes.IMPORT);
                        } else {
                            builder.advanceLexer();
                        }
                    }
                }
            }
            done(headingMarker, RobotTokenTypes.HEADING);
        }
    }

    private static void parseKeywordDefinition(@NotNull PsiBuilder builder) {
        PsiBuilder.Marker keywordMarker = null;
        PsiBuilder.Marker keywordIdMarker = null;
        while (true) {
            IElementType type = builder.getTokenType();
            if (RobotTokenTypes.KEYWORD_DEFINITION == type || (RobotTokenTypes.VARIABLE_DEFINITION == type && isNextToken(builder,
                                                                                                                          RobotTokenTypes.KEYWORD_DEFINITION))) {
                if (builder.rawLookup(-1) != RobotTokenTypes.VARIABLE_DEFINITION) {
                    done(keywordIdMarker, RobotTokenTypes.KEYWORD_STATEMENT);
                    done(keywordMarker, RobotTokenTypes.KEYWORD_DEFINITION);
                    keywordMarker = builder.mark();
                    keywordIdMarker = builder.mark();
                }
                if (RobotTokenTypes.KEYWORD_DEFINITION == type) {
                    builder.advanceLexer();
                }
            }
            if (builder.eof()) {
                done(keywordIdMarker, RobotTokenTypes.KEYWORD_STATEMENT);
                done(keywordMarker, RobotTokenTypes.KEYWORD_DEFINITION);
                break;
            } else {
                type = builder.getTokenType();
                // not all the time; all cases but VAR_DEF (when in keyword definition only)
                if (RobotTokenTypes.HEADING == type) {
                    done(keywordIdMarker, RobotTokenTypes.KEYWORD_STATEMENT);
                    done(keywordMarker, RobotTokenTypes.KEYWORD_DEFINITION);
                    break;
                } else if (RobotTokenTypes.BRACKET_SETTING == type) {
                    done(keywordIdMarker, RobotTokenTypes.KEYWORD_STATEMENT);
                    keywordIdMarker = null;
                    parseWithArguments(builder, RobotTokenTypes.BRACKET_SETTING);
                } else if (RobotTokenTypes.ERROR == type) {
                    builder.advanceLexer();
                } else if (RobotTokenTypes.VARIABLE_DEFINITION == type) {
                    PsiBuilder.Marker statement = parseKeywordStatement(builder, RobotTokenTypes.VARIABLE_DEFINITION, true);
                    if (statement != null && keywordIdMarker != null) {
                        keywordIdMarker.doneBefore(RobotTokenTypes.KEYWORD_STATEMENT, statement);
                        keywordIdMarker = null;
                    }
                } else {
                    done(keywordIdMarker, RobotTokenTypes.KEYWORD_STATEMENT);
                    keywordIdMarker = null;
                    parseKeywordStatement(builder, RobotTokenTypes.KEYWORD_STATEMENT, false);
                }
            }
        }
    }

    private static void parseWithArguments(@NotNull PsiBuilder builder, @NotNull IElementType markType) {
        IElementType type = builder.getTokenType();
        PsiBuilder.Marker marker = builder.mark();
        PsiBuilder.Marker id = null;
        if (type == RobotTokenTypes.VARIABLE_DEFINITION) {
            id = builder.mark();
        }
        builder.advanceLexer();
        if (id != null) {
            id.done(RobotTokenTypes.VARIABLE_DEFINITION_ID);
        }
        while (!builder.eof()) {
            type = builder.getTokenType();
            if (RobotTokenTypes.ARGUMENT == type || RobotTokenTypes.VARIABLE == type) {
                parseWith(builder, RobotTokenTypes.ARGUMENT);
            } else if (RobotTokenTypes.VARIABLE_DEFINITION == type && (builder.rawLookup(-1) != RobotTokenTypes.WHITESPACE
                                                                       || builder.rawLookup(-2) != RobotTokenTypes.WHITESPACE
                                                                       || builder.rawLookup(-3) == RobotTokenTypes.WHITESPACE)) {
                int currentOffset = builder.getCurrentOffset();
                String originalText = builder.getOriginalText().toString();
                String[] lines = originalText.split(System.lineSeparator());
                int lineIndex = getLineIndex(originalText, currentOffset);

                if (getLineIndex(originalText, currentOffset) == getLineIndex(originalText, builder.getCurrentOffset()) || lines[lineIndex].stripLeading()
                                                                                                                                           .startsWith("...")) {
                    parseVariableDefinitionWithDefaults(builder);
                }
            } else {
                break;
            }
        }
        marker.done(markType);
    }

    private static PsiBuilder.Marker parseKeywordStatement(@NotNull PsiBuilder builder, @NotNull IElementType rootType, boolean isGherkin) {
        PsiBuilder.Marker marker = builder.mark();
        boolean keywordFound = false;
        boolean inline = false;

        while (!builder.eof()) {
            IElementType tokenType = builder.getTokenType();
            if (tokenType == RobotTokenTypes.GHERKIN) {
                if (!isGherkin && !keywordFound) {
                    isGherkin = true;
                    builder.advanceLexer();
                    continue;
                }
                break;
            }

            if (tokenType == RobotTokenTypes.KEYWORD || (tokenType == RobotTokenTypes.VARIABLE && isNextToken(builder, RobotTokenTypes.KEYWORD))) {
                if (keywordFound) {
                    break;
                }
                keywordFound = true;
                parseKeyword(builder);
                continue;
            }

            if ((tokenType == RobotTokenTypes.ARGUMENT || tokenType == RobotTokenTypes.VARIABLE) && builder.rawLookup(1) != RobotTokenTypes.KEYWORD) {
                parseWith(builder, RobotTokenTypes.ARGUMENT);
            } else if (tokenType == RobotTokenTypes.VARIABLE_DEFINITION) {
                if (keywordFound) {
                    break;
                }

                keywordFound = true;
                boolean isKeywordDefinition =
                        builder.rawLookup(-1) == RobotTokenTypes.KEYWORD_DEFINITION || isNextToken(builder, RobotTokenTypes.KEYWORD_DEFINITION);
                PsiBuilder.Marker variableMarker = builder.mark();
                builder.advanceLexer();
                done(variableMarker, RobotTokenTypes.VARIABLE_DEFINITION_ID);
                inline = isKeywordDefinition;

                if (!isKeywordDefinition && builder.getTokenType() == RobotTokenTypes.KEYWORD) {
                    parseKeywordStatement(builder, RobotTokenTypes.KEYWORD_STATEMENT, true);
                }
            } else if (tokenType == RobotTokenTypes.SYNTAX_MARKER && !keywordFound) {
                keywordFound = true;
                parseWith(builder, RobotTokenTypes.SYNTAX_MARKER);
            } else {
                break;
            }
        }

        marker.done(rootType);
        return inline ? null : marker;
    }

    private static void parseKeyword(@NotNull PsiBuilder builder) {
        int offset = 1;
        boolean hasVariable = false;
        IElementType nextTokenType;

        while ((nextTokenType = builder.rawLookup(offset)) != null && nextTokenType != RobotTokenTypes.WHITESPACE) {
            if (nextTokenType == RobotTokenTypes.KEYWORD || nextTokenType == RobotTokenTypes.VARIABLE) {
                if (nextTokenType == RobotTokenTypes.VARIABLE) {
                    hasVariable = true;
                }
                offset++;
                continue;
            }
            break;
        }

        if (builder.rawLookup(0) == RobotTokenTypes.KEYWORD && (offset > 1 && hasVariable)) {
            PsiBuilder.Marker keywordMarker = builder.mark();
            IElementType currentTokenType = builder.getTokenType();

            while (!builder.eof()) {
                boolean isWhitespace = isNextToken(builder, RobotTokenTypes.WHITESPACE);
                if (currentTokenType == RobotTokenTypes.VARIABLE) {
                    parseSimple(builder, currentTokenType);
                } else if (!isWhitespace) {
                    builder.remapCurrentToken(RobotTokenTypes.KEYWORD_PART);
                    parseSimple(builder, RobotTokenTypes.KEYWORD_PART);
                } else {
                    builder.advanceLexer();
                }
                if (!isWhitespace) {
                    currentTokenType = builder.getTokenType();
                }
            }
            keywordMarker.done(RobotTokenTypes.KEYWORD);
        } else {
            parseWith(builder, RobotTokenTypes.KEYWORD);
        }
    }

    private static boolean isNextToken(@NotNull PsiBuilder builder, IElementType type) {
        boolean allowEof = type == RobotTokenTypes.WHITESPACE;
        IElementType next = builder.rawLookup(1);
        return next == type || allowEof && next == null;
    }

    private static int getLineIndex(String text, int offset) {
        String[] lines = text.split(System.lineSeparator());
        int currentLength = 0;
        for (int i = 0; i < lines.length; i++) {
            currentLength += lines[i].length() + System.lineSeparator().length();
            if (currentLength > offset) {
                return i;
            }
        }
        return -1;
    }

    private static void parseSetting(@NotNull PsiBuilder builder) {
        PsiBuilder.Marker settingsMarker = builder.mark();
        PsiBuilder.Marker id = null;
        if (builder.getTokenType() == RobotTokenTypes.VARIABLE_DEFINITION) {
            id = builder.mark();
        }
        builder.advanceLexer();
        if (id != null) {
            id.done(RobotTokenTypes.VARIABLE_DEFINITION_ID);
        }
        while (!builder.eof()) {
            IElementType type = builder.getTokenType();
            if (RobotTokenTypes.ARGUMENT == type || RobotTokenTypes.VARIABLE == type) {
                parseWith(builder, RobotTokenTypes.ARGUMENT);
            } else if (RobotTokenTypes.SYNTAX_MARKER == type) {
                parseWith(builder, RobotTokenTypes.SYNTAX_MARKER);
            } else if (RobotTokenTypes.VARIABLE_DEFINITION == type) {
                parseWith(builder, RobotTokenTypes.VARIABLE_DEFINITION);
            } else {
                break;
            }
        }
        settingsMarker.done(RobotTokenTypes.SETTING);
    }

    private static void parseVariableDefinitionWithDefaults(@NotNull PsiBuilder builder) {
        PsiBuilder.Marker argMarker = builder.mark();
        PsiBuilder.Marker definitionMarker = builder.mark();
        PsiBuilder.Marker definitionIdMarker = builder.mark();

        builder.advanceLexer();

        definitionIdMarker.done(RobotTokenTypes.VARIABLE_DEFINITION_ID);
        definitionMarker.done(RobotTokenTypes.VARIABLE_DEFINITION);

        IElementType token = builder.getTokenType();
        while (!builder.eof() && (token == RobotTokenTypes.ARGUMENT || token == RobotTokenTypes.VARIABLE)) {
            PsiBuilder.Marker variableMarker = null;
            if (token == RobotTokenTypes.VARIABLE) {
                variableMarker = builder.mark();
            }
            builder.advanceLexer();
            if (token == RobotTokenTypes.VARIABLE) {
                done(variableMarker, RobotTokenTypes.VARIABLE);
            }
            token = builder.getTokenType();
        }
        argMarker.done(RobotTokenTypes.ARGUMENT);
    }

    private static void parseWith(@NotNull PsiBuilder builder, @NotNull IElementType type) {
        PsiBuilder.Marker arg = builder.mark();
        IElementType current = builder.getTokenType();
        while (!builder.eof() && (type == current || RobotTokenTypes.VARIABLE == current || RobotTokenTypes.VARIABLE_DEFINITION == current)) {
            boolean end = isNextToken(builder, RobotTokenTypes.WHITESPACE);
            if (RobotTokenTypes.VARIABLE == current || RobotTokenTypes.VARIABLE_DEFINITION == current) {
                parseSimple(builder, current);
            } else {
                builder.advanceLexer();
            }
            if (end) {
                break;
            }
            current = builder.getTokenType();
        }
        arg.done(type);
    }

    private static void parseSimple(@NotNull PsiBuilder builder, @NotNull IElementType type) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer();
        marker.done(type);
    }

    private static void done(@Nullable PsiBuilder.Marker marker, @NotNull RobotElementType type) {
        if (marker != null) {
            marker.done(type);
        }
    }
}
