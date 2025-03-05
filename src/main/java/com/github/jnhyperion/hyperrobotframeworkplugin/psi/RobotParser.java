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
                        } else if (RobotStubTokenTypes.VARIABLE_DEFINITION == type && isNextToken(builder, RobotTokenTypes.WHITESPACE)) {
                            parseWithArguments(builder, RobotStubTokenTypes.VARIABLE_DEFINITION);
                        } else if (RobotTokenTypes.BRACKET_SETTING == type) {
                            parseWithArguments(builder, RobotTokenTypes.BRACKET_SETTING);
                        } else if (RobotStubTokenTypes.KEYWORD_DEFINITION == type ||
                                   (RobotStubTokenTypes.VARIABLE_DEFINITION == type && isNextToken(builder, RobotStubTokenTypes.KEYWORD_DEFINITION))) {
                            parseKeywordDefinition(builder);
                        } else if (RobotTokenTypes.KEYWORD == type) {
                            parseKeywordStatement(builder, RobotStubTokenTypes.KEYWORD_STATEMENT, false);
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
            if (RobotStubTokenTypes.KEYWORD_DEFINITION == type ||
                (RobotStubTokenTypes.VARIABLE_DEFINITION == type && isNextToken(builder, RobotStubTokenTypes.KEYWORD_DEFINITION))) {
                if (builder.rawLookup(-1) != RobotStubTokenTypes.VARIABLE_DEFINITION) {
                    done(keywordIdMarker, RobotTokenTypes.KEYWORD_DEFINITION_ID);
                    done(keywordMarker, RobotStubTokenTypes.KEYWORD_DEFINITION);
                    keywordMarker = builder.mark();
                    keywordIdMarker = builder.mark();
                }
                if (RobotStubTokenTypes.KEYWORD_DEFINITION == type) {
                    builder.advanceLexer();
                }
            }
            if (builder.eof()) {
                done(keywordIdMarker, RobotTokenTypes.KEYWORD_DEFINITION_ID);
                done(keywordMarker, RobotStubTokenTypes.KEYWORD_DEFINITION);
                break;
            } else {
                type = builder.getTokenType();
                // not all the time; all cases but VAR_DEF (when in keyword definition only)
                if (RobotTokenTypes.HEADING == type) {
                    done(keywordIdMarker, RobotTokenTypes.KEYWORD_DEFINITION_ID);
                    done(keywordMarker, RobotStubTokenTypes.KEYWORD_DEFINITION);
                    break;
                } else if (RobotTokenTypes.BRACKET_SETTING == type) {
                    done(keywordIdMarker, RobotTokenTypes.KEYWORD_DEFINITION_ID);
                    keywordIdMarker = null;
                    parseWithArguments(builder, RobotTokenTypes.BRACKET_SETTING);
                } else if (RobotTokenTypes.ERROR == type) {
                    builder.advanceLexer();
                } else if (RobotStubTokenTypes.VARIABLE_DEFINITION == type) {
                    PsiBuilder.Marker statement = parseKeywordStatement(builder, RobotStubTokenTypes.VARIABLE_DEFINITION, true);
                    if (statement != null && keywordIdMarker != null) {
                        keywordIdMarker.doneBefore(RobotTokenTypes.KEYWORD_DEFINITION_ID, statement);
                        keywordIdMarker = null;
                    }
                } else {
                    done(keywordIdMarker, RobotTokenTypes.KEYWORD_DEFINITION_ID);
                    keywordIdMarker = null;
                    parseKeywordStatement(builder, RobotStubTokenTypes.KEYWORD_STATEMENT, false);
                }
            }
        }
    }

    private static void parseWithArguments(@NotNull PsiBuilder builder, @NotNull IElementType markType) {
        IElementType type = builder.getTokenType();
        PsiBuilder.Marker marker = builder.mark();
        PsiBuilder.Marker id = null;
        if (type == RobotStubTokenTypes.VARIABLE_DEFINITION) {
            id = builder.mark();
        }
        builder.advanceLexer();
        if (id != null) {
            id.done(RobotTokenTypes.VARIABLE_DEFINITION_ID);
        }
        while (!builder.eof()) {
            type = builder.getTokenType();
            if (RobotTokenTypes.PARAMETER == type) {
                parseWith(builder, RobotTokenTypes.PARAMETER);
            } else if (RobotTokenTypes.ARGUMENT == type || RobotTokenTypes.VARIABLE == type) {
                parseWith(builder, RobotTokenTypes.ARGUMENT);
            } else if (RobotStubTokenTypes.VARIABLE_DEFINITION == type && RobotStubTokenTypes.VARIABLE_DEFINITION != markType) {
                if (builder.rawLookup(-1) != RobotTokenTypes.WHITESPACE &&
                    builder.rawLookup(-2) != RobotTokenTypes.WHITESPACE &&
                    builder.rawLookup(-3) == RobotTokenTypes.WHITESPACE) {
                    break;
                }
                int currentOffset = builder.getCurrentOffset();
                String originalText = builder.getOriginalText().toString();
                String[] lines = originalText.split(System.lineSeparator());
                int lineIndex = getLineIndex(originalText, currentOffset);

                if (lines[lineIndex].stripLeading().startsWith("...")) {
                    parseVariableDefinitionWithDefaults(builder);
                } else {
                    break;
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

            if (tokenType == RobotTokenTypes.PARAMETER) {
                parseWith(builder, RobotTokenTypes.PARAMETER);
            } else if ((tokenType == RobotTokenTypes.ARGUMENT || tokenType == RobotTokenTypes.VARIABLE) && builder.rawLookup(1) != RobotTokenTypes.KEYWORD) {
                parseWith(builder, RobotTokenTypes.ARGUMENT);
            } else if (tokenType == RobotStubTokenTypes.VARIABLE_DEFINITION) {
                if (keywordFound) {
                    break;
                }

                keywordFound = true;
                boolean isKeywordDefinition = builder.rawLookup(-1) == RobotStubTokenTypes.KEYWORD_DEFINITION ||
                                              isNextToken(builder, RobotStubTokenTypes.KEYWORD_DEFINITION);
                PsiBuilder.Marker variableMarker = builder.mark();
                builder.advanceLexer();
                done(variableMarker, RobotTokenTypes.VARIABLE_DEFINITION_ID);
                inline = isKeywordDefinition;

                if (!isKeywordDefinition && builder.getTokenType() == RobotTokenTypes.KEYWORD) {
                    parseKeywordStatement(builder, RobotStubTokenTypes.KEYWORD_STATEMENT, true);
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
        return isNextToken(builder, 1, type);
    }

    private static boolean isNextToken(@NotNull PsiBuilder builder, int nextTokenCount, IElementType type) {
        nextTokenCount = Math.max(1, nextTokenCount);
        boolean allowEof = type == RobotTokenTypes.WHITESPACE;
        IElementType next = builder.rawLookup(nextTokenCount);
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
        if (builder.getTokenType() == RobotStubTokenTypes.VARIABLE_DEFINITION) {
            id = builder.mark();
        }
        builder.advanceLexer();
        if (id != null) {
            id.done(RobotTokenTypes.VARIABLE_DEFINITION_ID);
        }
        while (!builder.eof()) {
            IElementType type = builder.getTokenType();
            if (RobotTokenTypes.PARAMETER == type) {
                parseWith(builder, RobotTokenTypes.PARAMETER);
            } else if (RobotTokenTypes.ARGUMENT == type || RobotTokenTypes.VARIABLE == type) {
                parseWith(builder, RobotTokenTypes.ARGUMENT);
            } else if (RobotTokenTypes.SYNTAX_MARKER == type) {
                parseWith(builder, RobotTokenTypes.SYNTAX_MARKER);
            } else if (RobotStubTokenTypes.VARIABLE_DEFINITION == type) {
                parseWith(builder, RobotStubTokenTypes.VARIABLE_DEFINITION);
            } else if (RobotTokenTypes.KEYWORD == type) {
                parseKeyword(builder);
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
        definitionMarker.done(RobotStubTokenTypes.VARIABLE_DEFINITION);

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
        argMarker.done(RobotStubTokenTypes.VARIABLE_DEFINITION);
    }

    private static void parseWith(@NotNull PsiBuilder builder, @NotNull IElementType type) {
        PsiBuilder.Marker arg = builder.mark();
        IElementType current = builder.getTokenType();
        PsiBuilder.Marker parameterId = null;
        while (!builder.eof() &&
               (type == current ||
                type == RobotTokenTypes.PARAMETER && current == RobotTokenTypes.ARGUMENT ||
                RobotTokenTypes.VARIABLE == current ||
                RobotStubTokenTypes.VARIABLE_DEFINITION == current)) {
            boolean end = (current != RobotTokenTypes.PARAMETER || isNextToken(builder, 2, RobotTokenTypes.WHITESPACE)) &&
                          isNextToken(builder, RobotTokenTypes.WHITESPACE);
            if (type == RobotTokenTypes.PARAMETER && current == RobotTokenTypes.ARGUMENT ||
                RobotTokenTypes.VARIABLE == current ||
                RobotStubTokenTypes.VARIABLE_DEFINITION == current) {
                parseSimple(builder, current);
            } else {
                if (current == RobotTokenTypes.PARAMETER) {
                    parameterId = builder.mark();
                }
                builder.advanceLexer();
                if (parameterId != null) {
                    parameterId.done(RobotTokenTypes.PARAMETER_ID);
                    parameterId = null;
                }
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

    private static void done(@Nullable PsiBuilder.Marker marker, @NotNull IElementType type) {
        if (marker != null) {
            marker.done(type);
        }
    }
}
