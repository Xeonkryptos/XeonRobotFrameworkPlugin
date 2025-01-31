package com.github.jnhyperion.hyperrobotframeworkplugin.psi;

import com.intellij.lexer.LexerBase;
import com.intellij.openapi.util.text.Strings;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class RobotLexer extends LexerBase {

    public static final int NONE = 0;
    public static final int SETTINGS_HEADING = 1;
    public static final int TEST_CASES_HEADING = 2;
    public static final int KEYWORDS_HEADING = 3;
    public static final int VARIABLES_HEADING = 4;
    public static final int IMPORT = 5;
    public static final int KEYWORD = 6;
    public static final int SETTING = 7;
    public static final int KEYWORD_DEFINITION = 8;
    public static final int VARIABLE_DEFINITION = 9;
    public static final int SYNTAX = 10;
    public static final int GHERKIN = 11;
    public static final int IF_CLAUSE = 12;

    private CharSequence buffer = Strings.EMPTY_CHAR_SEQUENCE;
    private int startOffset;
    private int endOffset;
    private int position;
    private IElementType currentToken;
    private final Stack<Integer> level = new Stack<>();
    private boolean isTestTemplate = false;
    private boolean isBracketTemplate = false;

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.position = startOffset;
        if (initialState == NONE) {
            this.level.clear();
        }

        this.isTestTemplate = false;
        this.isBracketTemplate = false;
        this.advance();
    }

    @Override
    public void advance() {
        if (this.position >= this.endOffset) {
            this.currentToken = null;
        } else {
            this.startOffset = this.position;
            int state = getState();
            int parentState = this.level.size() > SETTINGS_HEADING ? this.level.get(this.level.size() - TEST_CASES_HEADING) : NONE;

            if (this.isComment(this.position)) {
                if (this.isNewLine(this.position)) {
                    this.currentToken = RobotTokenTypes.WHITESPACE;
                    this.position++;
                } else if (this.isSuperSpace(this.position)) {
                    skipWhitespace();
                    this.currentToken = RobotTokenTypes.WHITESPACE;
                } else {
                    this.currentToken = RobotTokenTypes.COMMENT;
                    goToEndOfLine();
                }
            } else {
                int currentPosition = this.position;
                if (isNewLine(currentPosition)) {
                    if (KEYWORD != state && IMPORT != state && SYNTAX != state && VARIABLE_DEFINITION != state && SETTING != state && IF_CLAUSE != state) {
                        if (KEYWORD_DEFINITION == state) {
                            int nextPosition = this.position + SETTINGS_HEADING;
                            if (!this.isWhitespace(nextPosition) && !isNewLine(nextPosition) && !isComment(nextPosition)) {
                                this.level.pop();
                                advance();
                                return;
                            }
                        }
                    } else if (!this.isEllipsis(this.position)) {
                        this.level.pop();
                        advance();
                        return;
                    }

                    this.currentToken = RobotTokenTypes.WHITESPACE;
                    this.position++;
                } else if (isHeading(this.position)) {
                    goToEndOfLine();
                    String line = getTokenText();
                    this.currentToken = RobotTokenTypes.HEADING;

                    if (isSettings(line)) {
                        this.level.clear();
                        this.level.push(SETTINGS_HEADING);
                    } else if (isTestCases(line)) {
                        this.level.clear();
                        this.level.push(TEST_CASES_HEADING);
                    } else if (isKeywords(line)) {
                        this.level.clear();
                        this.level.push(KEYWORDS_HEADING);
                    } else if (isVariables(line)) {
                        this.level.clear();
                        this.level.push(VARIABLES_HEADING);
                    } else {
                        this.currentToken = RobotTokenTypes.ERROR;
                    }
                }

                // the rest is based on state
                if (state == NONE) {
                    // this is the instance where no '*** setting ***' has been detected yet.
                    goToEndOfLine();
                    this.currentToken = RobotTokenTypes.ERROR; // TODO: COMMENT?
                } else {
                    if (SETTINGS_HEADING == state) {
                        if (isSuperSpace(this.position)) {
                            skipWhitespace();
                        }

                        goToNextNewLineOrSuperSpace();
                        String word = getTokenText();
                        if (RobotKeywordProvider.isSyntaxOfType(RobotTokenTypes.IMPORT, word)) {
                            this.level.push(IMPORT);
                            this.currentToken = RobotTokenTypes.IMPORT;
                        } else {
                            if (!RobotKeywordProvider.isGlobalSetting(word)) {
                                this.goToEndOfLine();
                                this.currentToken = RobotTokenTypes.ERROR;
                            } else {
                                this.currentToken = RobotTokenTypes.SETTING;
                                if (RobotKeywordProvider.isSyntaxFollowedByKeyword(word)) {
                                    if (RobotKeywordProvider.isTestTemplate(word)) {
                                        this.isTestTemplate = true;
                                    }

                                    this.level.push(SYNTAX);
                                } else if (RobotKeywordProvider.isSyntaxFollowedByVariableDefinition(word)) {
                                    this.level.push(SETTING);
                                } else if (RobotKeywordProvider.isSyntaxFollowedByString(word)) {
                                    this.level.push(IMPORT);
                                } else {
                                    this.goToEndOfLine();
                                    this.currentToken = RobotTokenTypes.ERROR;
                                }
                            }
                        }
                    } else {
                        if (VARIABLES_HEADING == state) {
                            if (isSuperSpace(this.position)) {
                                skipWhitespace();
                            }

                            goToVariableEnd();
                            this.level.push(VARIABLE_DEFINITION);
                            this.currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
                        } else if (TEST_CASES_HEADING != state && KEYWORDS_HEADING != state) {
                            if (KEYWORD_DEFINITION != state) {
                                if (KEYWORD != state && IMPORT != state && VARIABLE_DEFINITION != state && SETTING != state && IF_CLAUSE != state) {
                                    if (SYNTAX == state) {
                                        if (isSuperSpace(this.position)) {
                                            skipWhitespace();
                                            this.currentToken = RobotTokenTypes.WHITESPACE;
                                        } else {
                                            goToNextNewLineOrSuperSpace();
                                            this.level.push(KEYWORD);
                                            this.currentToken = RobotTokenTypes.KEYWORD;
                                        }
                                    } else if (GHERKIN == state) {
                                        this.level.pop();
                                        this.currentToken = RobotTokenTypes.WHITESPACE;
                                        this.position++;
                                    } else {
                                        throw new RuntimeException("Unknown State: " + state);
                                    }
                                } else if (isSuperSpace(this.position)) {
                                    skipWhitespace();
                                    this.currentToken = RobotTokenTypes.WHITESPACE;
                                    if (KEYWORD == state && KEYWORD == parentState) {
                                        this.level.pop();
                                    }
                                } else {
                                    if (this.isEllipsis(this.position)) {
                                        if (isOnlyWhitespaceToPreviousLine(position - 1)) {
                                            // if the only thing before the ... is white space then it is the reserved word
                                            goToNextNewLineOrSuperSpace();
                                            this.currentToken = RobotTokenTypes.WHITESPACE;
                                        } else {
                                            goToNextNewLineOrSuperSpace();
                                            this.currentToken = RobotTokenTypes.ARGUMENT;
                                        }
                                    } else if (VARIABLE_DEFINITION == state && isVariableEnd(this.position - 1)) {
                                        goToNextNewLineOrSuperSpace();
                                        this.currentToken = RobotTokenTypes.WHITESPACE;
                                    } else if (VARIABLE_DEFINITION == state && KEYWORD_DEFINITION == parentState) {
                                        // this is a variable assignment inside a keyword definition: "${var} =  some keyword  arg1  arg2"
                                        // next token may be another variable or a keyword
                                        if (this.isVariable(this.position)) {
                                            this.goToVariableEnd();
                                            this.currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
                                            return;
                                        } else {
                                            this.goToNextNewLineOrSuperSpace();
                                            if (this.getTokenText().equals("IF")) {
                                                this.currentToken = RobotTokenTypes.SYNTAX_MARKER;
                                                this.level.push(IF_CLAUSE);
                                                return;
                                            }
                                        }

                                        this.currentToken = RobotTokenTypes.KEYWORD;
                                        this.level.push(KEYWORD);
                                    } else if (isVariable(this.position)) {
                                        this.goToVariableEnd();
                                        if (SETTING == state && isSuperSpacePrevious()) {
                                            this.currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
                                        } else if (!isWithinForLoop() && !isVarOrAsTokenPresent(this.position)) {
                                            this.currentToken = RobotTokenTypes.VARIABLE;
                                        } else {
                                            this.currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
                                            this.level.push(VARIABLE_DEFINITION);
                                        }
                                    } else if (KEYWORD == state && KEYWORD == parentState) {
                                        this.goToNextNewLineOrSuperSpaceOrVariable();
                                        this.currentToken = RobotTokenTypes.KEYWORD;
                                    } else {
                                        this.goToNextNewLineOrSuperSpaceOrVariable();
                                        String word = this.getTokenText();
                                        if (this.startOffset == NONE || this.charAtEquals(this.startOffset - SETTINGS_HEADING, '\n')) {
                                            this.currentToken = RobotTokenTypes.KEYWORD_DEFINITION;
                                            this.isBracketTemplate = false;
                                        } else if (RobotKeywordProvider.isSyntaxOfType(RobotTokenTypes.SYNTAX_MARKER, word)) {
                                            this.currentToken = RobotTokenTypes.SYNTAX_MARKER;
                                            this.level.push(IF_CLAUSE);
                                        } else {
                                            if (parentState != IMPORT || state != IF_CLAUSE) {
                                                this.currentToken = RobotTokenTypes.ARGUMENT;
                                            } else {
                                                this.currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (this.isSuperSpace(this.position)) {
                                    this.skipWhitespace();
                                    this.currentToken = RobotTokenTypes.WHITESPACE;
                                } else if (this.isVariable(this.position)) {
                                    this.goToVariableEnd();
                                    if (this.isSuperSpaceOrNewline(this.position) || isVariableDefinition(this.position)) {
                                        if ((this.isTestTemplate || this.isBracketTemplate) && parentState == TEST_CASES_HEADING) {
                                            this.currentToken = RobotTokenTypes.VARIABLE;
                                            this.level.push(KEYWORD);
                                            return;
                                        } else {
                                            this.currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
                                            this.level.push(VARIABLE_DEFINITION);
                                        }
                                    } else {
                                        this.currentToken = RobotTokenTypes.VARIABLE;
                                        this.level.push(KEYWORD);
                                        if (!this.isSuperSpaceOrNewline(this.position)) {
                                            this.level.push(KEYWORD);
                                        }
                                    }
                                } else {
                                    skipWhitespace();

                                    String word = getTokenText();
                                    if (RobotKeywordProvider.isSyntaxOfType(RobotTokenTypes.GHERKIN, word)) {
                                        this.currentToken = RobotTokenTypes.GHERKIN;
                                        this.level.push(11);
                                    } else {
                                        this.goToNextNewLineOrSuperSpaceOrVariable();
                                        word = getTokenText();
                                        if (RobotKeywordProvider.isSyntaxOfType(RobotTokenTypes.BRACKET_SETTING, word)) {
                                            this.currentToken = RobotTokenTypes.BRACKET_SETTING;
                                            if (RobotKeywordProvider.isSyntaxFollowedByKeyword(word)) {
                                                if ("[Template]".equals(word)) {
                                                    this.isBracketTemplate = true;
                                                }

                                                this.level.push(SYNTAX);
                                            } else if (RobotKeywordProvider.isSyntaxFollowedByVariableDefinition(word)) {
                                                this.level.push(SETTING);
                                            } else if (RobotKeywordProvider.isSyntaxFollowedByString(word)) {
                                                this.level.push(IMPORT);
                                            } else {
                                                goToEndOfLine();
                                                this.currentToken = RobotTokenTypes.ERROR;
                                            }
                                        } else if (RobotKeywordProvider.isSyntaxOfType(RobotTokenTypes.SYNTAX_MARKER, word)) {
                                            this.currentToken = RobotTokenTypes.SYNTAX_MARKER;
                                            this.level.push(IF_CLAUSE);
                                        } else {
                                            if (!this.isTestTemplate && !this.isBracketTemplate || parentState != TEST_CASES_HEADING) {
                                                this.currentToken = RobotTokenTypes.KEYWORD;
                                                this.level.push(KEYWORD);
                                                if (!this.isSuperSpaceOrNewline(this.position)) {
                                                    this.level.push(KEYWORD);
                                                }
                                            } else {
                                                this.currentToken = RobotTokenTypes.ARGUMENT;
                                                this.level.push(KEYWORD);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if (this.isSuperSpace(this.position)) {
                                this.skipWhitespace();
                            }
                            if (this.isVariable(this.position)) {
                                this.goToVariableEnd();
                                this.currentToken = RobotTokenTypes.VARIABLE_DEFINITION;
                            } else {
                                this.goToNextNewLineOrSuperSpaceOrVariable();
                                this.currentToken = RobotTokenTypes.KEYWORD_DEFINITION;
                                this.isBracketTemplate = false;
                            }
                            if (this.isSuperSpaceOrNewline(this.position)) {
                                this.level.push(KEYWORD_DEFINITION);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isWithinForLoop() {
        int startOffset = this.startOffset;
        List<TokenWord> tokenWords = this.extractTokenWords();
        if (!tokenWords.isEmpty() && tokenWords.get(NONE).word.equals("FOR")) {
            TokenWord forToken = tokenWords.get(NONE);
            TokenWord inToken = null;
            for (TokenWord token : tokenWords) {
                if (token.word.startsWith("IN")) {
                    inToken = token;
                    break;
                }
            }
            if (inToken != null) {
                return startOffset < inToken.start && startOffset > forToken.end;
            }
        }
        return false;
    }

    private boolean isVarOrAsTokenPresent(int startOffset) {
        List<TokenWord> tokenWords = this.extractTokenWords();
        int tokenCount = tokenWords.size();
        for (int i = 0; i < tokenCount; i++) {
            TokenWord token = tokenWords.get(i);
            if (("VAR".equals(token.word) || "AS".equals(token.word)) && i + 1 < tokenCount && tokenWords.get(i + 1).start == startOffset) {
                return true;
            }
        }
        return false;
    }

    private boolean isVariable(int var1) {
        if (this.isVariableStart(var1)) {
            var1 += TEST_CASES_HEADING;

            for (int var2 = SETTINGS_HEADING; var2 > NONE && var1 < this.endOffset && var1 >= NONE; var1++) {
                if (this.charAtEquals(var1, '}')) {
                    if (--var2 == NONE) {
                        return true;
                    }
                }

                if (this.isVariableStart(var1)) {
                    var2++;
                    var1 += TEST_CASES_HEADING;
                }

                if (this.isSuperSpaceOrNewline(var1)) {
                    return false;
                }
            }
        }

        return false;
    }

    private boolean isComment(int var1) {
        while (var1 < this.endOffset && (this.isWhitespace(var1) || this.charAtEquals(var1, '\n'))) {
            var1++;
        }

        return this.charAtEquals(var1, '#');
    }

    private boolean isVariableStart(int position) {
        return (this.charAtEquals(position, '$') || this.charAtEquals(position, '@') || this.charAtEquals(position, '&') || this.charAtEquals(position, '%'))
               && this.charAtEquals(position + 1, '{');
    }

    private List<TokenWord> extractTokenWords() {
        List<TokenWord> tokenWords = new ArrayList<>();
        int start = this.startOffset;

        // Move start to the beginning of the line
        while (start > 0 && !this.charAtEquals(start - 1, '\n')) {
            start--;
        }

        int end = start;

        // Move end to the end of the line
        while (end < this.endOffset && !this.charAtEquals(end, '\n')) {
            end++;
        }

        // Extract words from the line
        while (start < end) {
            // Skip whitespace
            while (start < end && Character.isWhitespace(this.buffer.charAt(start))) {
                start++;
            }

            int wordEnd = start;

            // Find the end of the word
            while (wordEnd < end && !this.isSuperSpaceOrNewline(wordEnd)) {
                wordEnd++;
            }

            // Add the word to the list if it's not empty
            String word = this.buffer.subSequence(start, wordEnd).toString();
            if (!word.isEmpty()) {
                tokenWords.add(new TokenWord(word, start, wordEnd));
            }

            start = wordEnd + 1;
        }

        return tokenWords;
    }

    private static boolean isSettings(String line) {
        return "*** Settings ***".equals(line) || "*** Setting ***".equals(line);
    }

    private static boolean isTestCases(String line) {
        return "*** Test Cases ***".equals(line) || "*** Test Case ***".equals(line);
    }

    private static boolean isKeywords(String line) {
        return "*** Keywords ***".equals(line) || "*** Keyword ***".equals(line);
    }

    private static boolean isVariables(String line) {
        return "*** Variables ***".equals(line) || "*** Variable ***".equals(line);
    }

    private boolean isHeading(int position) {
        return charAtEquals(position, '*') && charAtEquals(position + 1, '*') && charAtEquals(position + 2, '*') && isSpace(position + 3);
    }

    private boolean isEllipsis(int position) {
        while (this.position < this.endOffset && (isWhitespace(position) || isNewLine(position))) {
            position++;
        }
        return charAtEquals(position, '.') && charAtEquals(position + SETTINGS_HEADING, '.') && charAtEquals(position + TEST_CASES_HEADING, '.')
               && isSuperSpaceOrNewline(position + KEYWORDS_HEADING);
    }

    private boolean isSuperSpacePrevious() {
        int position = this.startOffset - 1;
        while (position >= 0 && !isWhitespace(position)) {
            if (!isWhitespace(position)) {
                return false;
            }
            position--;
        }
        return true;
    }

    private boolean isSuperSpace(int position) {
        return isSpace(position) && isSpace(position + SETTINGS_HEADING) || isSpace(position) && isTab(position + SETTINGS_HEADING) || isTab(position);
    }

    private boolean isTab(int position) {
        return charAtEquals(position, '\t');
    }

    private boolean isSpace(int position) {
        return charAtEquals(position, ' ');
    }

    private boolean isSuperSpaceOrNewline(int position) {
        return this.isSuperSpace(position) || isNewLine(position);
    }

    private boolean isVariableDefinition(int position) {
        return isSuperSpaceOrNewline(position) || charAtEquals(position, '=') && isSuperSpaceOrNewline(position + 1) || isSpace(position) && charAtEquals(
                position + 1,
                '=') && isSuperSpaceOrNewline(position + 2);
    }

    private void goToEndOfLine() {
        while (this.position < this.endOffset) {
            int var2 = this.position;
            if (!this.charAtEquals(var2, '\n')) {
                this.position++;
                continue;
            }
            break;
        }
    }

    @Override
    public int getState() {
        return this.level.isEmpty() ? NONE : this.level.peek();
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        return this.currentToken;
    }

    @Override
    public int getTokenStart() {
        return this.startOffset;
    }

    @Override
    public int getTokenEnd() {
        return this.position;
    }

    @NotNull
    @Override
    public CharSequence getBufferSequence() {
        return this.buffer;
    }

    @Override
    public int getBufferEnd() {
        return this.endOffset;
    }

    private boolean isVariableEnd(int position) {
        return charAtEquals(position, '}');
    }

    private void goToNextNewLineOrSuperSpace() {
        while (this.position < this.endOffset && !this.isSuperSpaceOrNewline(this.position)) {
            this.position++;
        }
    }

    private void goToNextNewLineOrSuperSpaceOrVariable() {
        while (this.position < this.endOffset && !this.isSuperSpaceOrNewline(this.position) && !this.isVariable(this.position)) {
            this.position++;
        }
    }

    private void goToVariableEnd() {
        int count = 0;
        if (this.isVariableStart(this.position)) {
            count++;
            this.position++;
        }

        while (this.position < this.endOffset && count > 0) {
            if (this.isVariableStart(this.position)) {
                count++;
            } else if (isVariableEnd(this.position)) {
                count--;
            }
            this.position++;
        }
    }

    private boolean isOnlyWhitespaceToPreviousLine(int position) {
        while (position >= 0 && !isNewLine(position)) {
            if (!isWhitespace(position)) {
                return false;
            }
            position--;
        }
        return true;
    }

    private void skipWhitespace() {
        while (this.position < this.endOffset && this.isWhitespace(this.position)) {
            this.position++;
        }
    }

    private boolean isWhitespace(int position) {
        return position < this.endOffset && !isNewLine(position) && Character.isWhitespace(this.buffer.charAt(position));
    }

    private boolean isNewLine(int position) {
        return charAtEquals(position, '\n');
    }

    private boolean charAtEquals(int position, char c) {
        return position < this.endOffset && this.buffer.charAt(position) == c;
    }

    private static class TokenWord {

        String word;
        int start;
        int end;

        TokenWord(String word, int start, int end) {
            this.word = word;
            this.start = start;
            this.end = end;
        }
    }
}
