package dev.xeonkryptos.xeonrobotframeworkplugin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RobotParser implements PsiParser {

    private String lineSeparator;

    @NotNull
    @Override
    public ASTNode parse(@NotNull IElementType type, @NotNull PsiBuilder builder) {
        lineSeparator = detectLineSeparator(builder.getOriginalText());

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

    private void parseHeading(@NotNull PsiBuilder builder) {
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
                    if (RobotTokenTypes.SETTING == type) {
                        parseSetting(builder);
                    } else if (RobotStubTokenTypes.VARIABLE_DEFINITION == type && isNextToken(builder, RobotTokenTypes.WHITESPACE)) {
                        parseWithArguments(builder, RobotStubTokenTypes.VARIABLE_DEFINITION);
                    } else if (RobotTokenTypes.BRACKET_SETTING == type) {
                        parseWithArguments(builder, RobotTokenTypes.BRACKET_SETTING);
                    } else if (RobotStubTokenTypes.KEYWORD_DEFINITION == type) {
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
            done(headingMarker, RobotTokenTypes.HEADING);
        }
    }

    private void parseKeywordDefinition(@NotNull PsiBuilder builder) {
        PsiBuilder.Marker keywordMarker = null;
        PsiBuilder.Marker keywordIdMarker = null;
        while (true) {
            IElementType type = builder.getTokenType();
            if (RobotStubTokenTypes.KEYWORD_DEFINITION == type) {
                done(keywordIdMarker, RobotTokenTypes.KEYWORD_DEFINITION_ID);
                done(keywordMarker, RobotStubTokenTypes.KEYWORD_DEFINITION);

                keywordMarker = builder.mark();
                keywordIdMarker = builder.mark();

                builder.advanceLexer();
                type = builder.getTokenType();
            }
            if (builder.eof() || RobotTokenTypes.HEADING == type) {
                done(keywordIdMarker, RobotTokenTypes.KEYWORD_DEFINITION_ID);
                done(keywordMarker, RobotStubTokenTypes.KEYWORD_DEFINITION);
                break;
            }

            if (RobotTokenTypes.BRACKET_SETTING == type) {
                done(keywordIdMarker, RobotTokenTypes.KEYWORD_DEFINITION_ID);
                keywordIdMarker = null;
                parseWithArguments(builder, RobotTokenTypes.BRACKET_SETTING);
            } else if (RobotTokenTypes.ERROR == type) {
                builder.advanceLexer();
            } else if (RobotStubTokenTypes.VARIABLE_DEFINITION == type) {
                PsiBuilder.Marker statement = parseKeywordStatement(builder, RobotTokenTypes.VARIABLE_DEFINITION_GROUP, true);
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

    private void parseWithArguments(@NotNull PsiBuilder builder, @NotNull IElementType markType) {
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
        String relevantLine = null;
        while (!builder.eof()) {
            type = builder.getTokenType();
            if (RobotTokenTypes.PARAMETER == type) {
                parseWith(builder, RobotTokenTypes.PARAMETER);
            } else if (RobotStubTokenTypes.ARGUMENT == type || RobotTokenTypes.VARIABLE == type) {
                parseWith(builder, RobotStubTokenTypes.ARGUMENT);
            } else if (RobotStubTokenTypes.VARIABLE_DEFINITION == type && RobotStubTokenTypes.VARIABLE_DEFINITION != markType) {
                if (builder.rawLookup(-1) != RobotTokenTypes.WHITESPACE && builder.rawLookup(-2) != RobotTokenTypes.WHITESPACE
                    && builder.rawLookup(-3) == RobotTokenTypes.WHITESPACE) {
                    break;
                }

                String tokenText = builder.getTokenText();
                if (relevantLine != null && relevantLine.equals(tokenText)) {
                    break;
                }

                int currentOffset = builder.getCurrentOffset();
                String originalText = builder.getOriginalText().toString();
                String lineText = getLineText(originalText, currentOffset);

                if (lineText != null && lineText.stripLeading().startsWith("...")) {
                    parseVariableDefinitionWithDefaults(builder);
                } else if (lineText != null && tokenText != null && !lineText.stripLeading().startsWith(tokenText)) {
                    relevantLine = tokenText;
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

    private PsiBuilder.Marker parseKeywordStatement(@NotNull PsiBuilder builder, @NotNull IElementType rootType, boolean isGherkin) {
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

            if (tokenType == RobotTokenTypes.KEYWORD || tokenType == RobotTokenTypes.VARIABLE && isVariableFollowedByKeyword(builder)) {
                if (keywordFound) {
                    break;
                }
                keywordFound = true;
                parseKeyword(builder);
                continue;
            }

            if (tokenType == RobotTokenTypes.PARAMETER) {
                parseWith(builder, RobotTokenTypes.PARAMETER);
            } else if ((tokenType == RobotStubTokenTypes.ARGUMENT || tokenType == RobotTokenTypes.VARIABLE)
                       && builder.rawLookup(1) != RobotTokenTypes.KEYWORD) {
                parseWith(builder, RobotStubTokenTypes.ARGUMENT);
            } else if (tokenType == RobotStubTokenTypes.VARIABLE_DEFINITION) {
                if (keywordFound) {
                    break;
                }

                keywordFound = getNextNonWhitespaceToken(builder) != RobotStubTokenTypes.VARIABLE_DEFINITION;
                boolean isKeywordDefinition =
                        builder.rawLookup(-1) == RobotStubTokenTypes.KEYWORD_DEFINITION || isNextToken(builder, RobotStubTokenTypes.KEYWORD_DEFINITION);
                PsiBuilder.Marker variableDefinitionMarker = null;
                if (rootType == RobotTokenTypes.VARIABLE_DEFINITION_GROUP) {
                    variableDefinitionMarker = builder.mark();
                }
                PsiBuilder.Marker variableIdMarker = builder.mark();
                builder.advanceLexer();
                done(variableIdMarker, RobotTokenTypes.VARIABLE_DEFINITION_ID);
                done(variableDefinitionMarker, RobotStubTokenTypes.VARIABLE_DEFINITION);
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

    private void parseKeyword(@NotNull PsiBuilder builder) {
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

    private boolean isNextToken(@NotNull PsiBuilder builder, IElementType type) {
        return isNextToken(builder, 1, type);
    }

    private boolean isNextToken(@NotNull PsiBuilder builder, int nextTokenCount, IElementType type) {
        nextTokenCount = Math.max(1, nextTokenCount);
        boolean allowEof = type == RobotTokenTypes.WHITESPACE;
        IElementType next = builder.rawLookup(nextTokenCount);
        return next == type || allowEof && next == null;
    }

    private boolean isVariableFollowedByKeyword(@NotNull PsiBuilder builder) {
        IElementType nextNonWhitespaceToken = getNextNonWhitespaceToken(builder);
        return nextNonWhitespaceToken == RobotTokenTypes.KEYWORD || nextNonWhitespaceToken == RobotTokenTypes.VARIABLE;
    }

    private IElementType getNextNonWhitespaceToken(@NotNull PsiBuilder builder) {
        int i = 1;
        IElementType nextToken;
        do {
            nextToken = builder.rawLookup(i);
            ++i;
        } while (nextToken == RobotTokenTypes.WHITESPACE);
        return nextToken;
    }

    private String getLineText(String originalText, int offset) {
        int currentLength = 0;
        String[] lines = originalText.split(lineSeparator);
        for (String line : lines) {
            currentLength += line.length() + lineSeparator.length();
            if (currentLength > offset) {
                return line;
            }
        }
        return null;
    }

    private void parseSetting(@NotNull PsiBuilder builder) {
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
            } else if (RobotStubTokenTypes.ARGUMENT == type || RobotTokenTypes.VARIABLE == type) {
                parseWith(builder, RobotStubTokenTypes.ARGUMENT);
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

    private void parseVariableDefinitionWithDefaults(@NotNull PsiBuilder builder) {
        PsiBuilder.Marker argMarker = builder.mark();
        PsiBuilder.Marker definitionMarker = builder.mark();
        PsiBuilder.Marker definitionIdMarker = builder.mark();

        builder.advanceLexer();

        definitionIdMarker.done(RobotTokenTypes.VARIABLE_DEFINITION_ID);
        definitionMarker.done(RobotStubTokenTypes.VARIABLE_DEFINITION);

        IElementType token = builder.getTokenType();
        while (!builder.eof() && (token == RobotStubTokenTypes.ARGUMENT || token == RobotTokenTypes.VARIABLE)) {
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

    private void parseWith(@NotNull PsiBuilder builder, @NotNull IElementType type) {
        PsiBuilder.Marker arg = builder.mark();
        IElementType current = builder.getTokenType();
        PsiBuilder.Marker parameterId = null;
        while (!builder.eof() && (type == current || type == RobotTokenTypes.PARAMETER && current == RobotStubTokenTypes.ARGUMENT
                                  || RobotTokenTypes.VARIABLE == current || RobotStubTokenTypes.VARIABLE_DEFINITION == current)) {
            boolean end = (current != RobotTokenTypes.PARAMETER || isNextToken(builder, 2, RobotTokenTypes.WHITESPACE)) && isNextToken(builder,
                                                                                                                                       RobotTokenTypes.WHITESPACE);
            if (type == RobotTokenTypes.PARAMETER && current == RobotStubTokenTypes.ARGUMENT || RobotTokenTypes.VARIABLE == current
                || RobotStubTokenTypes.VARIABLE_DEFINITION == current) {
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

    private void parseSimple(@NotNull PsiBuilder builder, @NotNull IElementType type) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer();
        marker.done(type);
    }

    private void done(@Nullable PsiBuilder.Marker marker, @NotNull IElementType type) {
        if (marker != null) {
            marker.done(type);
        }
    }

    private String detectLineSeparator(@NotNull CharSequence text) {
        int length = text.length();
        for (int i = 0; i < length; i++) {
            char c = text.charAt(i);
            if (c == '\n') {
                return i > 0 && text.charAt(i - 1) == '\r' ? "\r\n" : "\n";
            } else if (c == '\r') {
                return "\r";
            }
        }
        // Default to platform line separator if none detected
        return System.lineSeparator();
    }
}
