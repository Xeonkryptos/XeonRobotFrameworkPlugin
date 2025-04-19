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
    public static final int COMMENTS_HEADING = 5;
    public static final int IMPORT = 6;
    public static final int KEYWORD = 7;
    public static final int SETTING = 8;
    public static final int KEYWORD_DEFINITION = 9;
    public static final int VARIABLE_DEFINITION = 10;
    public static final int SYNTAX = 11;
    public static final int GHERKIN = 12;
    public static final int IF_CLAUSE = 13;

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
            int parentState = this.level.size() > 1 ? this.level.get(this.level.size() - 2) : NONE;

            if (isComment(this.position)) {
                handleComment();
            } else if (isNewLine(this.position)) {
                handleNewLine(state);
            } else if (isHeading(this.position)) {
                handleHeading();
            } else {
                handleState(state, parentState);
            }
        }
    }

    private void handleComment() {
        if (isNewLine(this.position)) {
            this.currentToken = RobotTokenTypes.WHITESPACE;
            this.position++;
        } else if (isSuperSpace(this.position)) {
            skipWhitespace();
            this.currentToken = RobotTokenTypes.WHITESPACE;
        } else {
            this.currentToken = RobotTokenTypes.COMMENT;
            goToEndOfLine();
        }
    }

    private void handleNewLine(int state) {
        if (state != KEYWORD && state != IMPORT && state != SYNTAX && state != VARIABLE_DEFINITION && state != SETTING && state != IF_CLAUSE) {
            if (state == KEYWORD_DEFINITION) {
                int nextPosition = this.position + 1;
                if (!isWhitespace(nextPosition) && !isNewLine(nextPosition) && !isComment(nextPosition)) {
                    this.level.pop();
                    advance();
                    return;
                }
            }
        } else if (!isEllipsis(this.position)) {
            this.level.pop();
            advance();
            return;
        }
        this.currentToken = RobotTokenTypes.WHITESPACE;
        this.position++;
    }

    private void handleHeading() {
        goToEndOfLine();
        String line = getTokenText();
        this.currentToken = RobotTokenTypes.HEADING;

        if (isSettings(line)) {
            this.level.clear();
            this.level.push(SETTINGS_HEADING);
        } else if (isComments(line)) {
            this.level.clear();
            this.level.push(COMMENTS_HEADING);
        } else if (isTestCases(line) || isTasks(line)) {
            this.level.clear();
            this.level.push(TEST_CASES_HEADING);
        } else if (isKeywords(line)) {
            this.isTestTemplate = false;
            this.level.clear();
            this.level.push(KEYWORDS_HEADING);
        } else if (isVariables(line)) {
            this.level.clear();
            this.level.push(VARIABLES_HEADING);
        } else {
            this.currentToken = RobotTokenTypes.ERROR;
        }
    }

    private void handleState(int state, int parentState) {
        if (state == NONE) {
            goToEndOfLine();
            this.currentToken = RobotTokenTypes.ERROR;
        } else if (state == SETTINGS_HEADING) {
            handleSettingsHeading();
        } else if (state == VARIABLES_HEADING) {
            handleVariablesHeading();
        } else if (state == COMMENTS_HEADING) {
            handleComment();
        } else if (state != TEST_CASES_HEADING && state != KEYWORDS_HEADING) {
            handleOtherStates(state, parentState);
        } else {
            handleKeywordDefinition(state, parentState);
        }
    }

    private void handleSettingsHeading() {
        if (isSuperSpace(this.position)) {
            skipWhitespace();
        }
        goToNextNewLineOrSuperSpace();
        String word = getTokenText();
        if (RobotKeywordProvider.isSyntaxOfType(RobotTokenTypes.IMPORT, word)) {
            this.level.push(IMPORT);
            this.currentToken = RobotTokenTypes.IMPORT;
        } else if (!RobotKeywordProvider.isGlobalSetting(word)) {
            goToEndOfLine();
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
                goToEndOfLine();
                this.currentToken = RobotTokenTypes.ERROR;
            }
        }
    }

    private void handleVariablesHeading() {
        if (isSuperSpace(this.position)) {
            skipWhitespace();
        }
        if (isVariable(this.position)) {
            goToVariableEnd();
            this.level.push(VARIABLE_DEFINITION);
            this.currentToken = RobotStubTokenTypes.VARIABLE_DEFINITION;
        } else {
            skipWhitespace();
            this.currentToken = RobotTokenTypes.WHITESPACE;
        }
    }

    private void handleOtherStates(int state, int parentState) {
        if (state == KEYWORD_DEFINITION) {
            handleKeywordDefinition(state, parentState);
        } else if (state == SYNTAX) {
            handleSyntax();
        } else if (state == GHERKIN) {
            handleGherkin();
        } else {
            handleRemainingStates(state, parentState);
        }
    }

    private void handleKeywordDefinition(int state, int parentState) {
        if (isSuperSpace(this.position)) {
            skipWhitespace();
            this.currentToken = RobotTokenTypes.WHITESPACE;
        } else if (isVariable(this.position)) {
            goToVariableEnd();
            if (isSuperSpaceOrNewline(this.position) || isVariableDefinition(this.position)) {
                if ((this.isTestTemplate || this.isBracketTemplate) && state == TEST_CASES_HEADING) {
                    this.currentToken = RobotTokenTypes.VARIABLE;
                    this.level.push(KEYWORD);
                } else {
                    this.currentToken = RobotStubTokenTypes.VARIABLE_DEFINITION;
                    this.level.push(VARIABLE_DEFINITION);
                }
            } else {
                this.currentToken = RobotTokenTypes.VARIABLE;
                this.level.push(KEYWORD);
                if (!isSuperSpaceOrNewline(this.position)) {
                    this.level.push(KEYWORD);
                }
            }
        } else {
            skipWhitespace();
            String word = getTokenText();
            if (RobotKeywordProvider.isSyntaxOfType(RobotTokenTypes.GHERKIN, word)) {
                this.currentToken = RobotTokenTypes.GHERKIN;
                this.level.push(GHERKIN);
            } else {
                goToNextNewLineOrSuperSpaceOrVariable();
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
                } else if (state == TEST_CASES_HEADING || state == KEYWORDS_HEADING) {
                    if (isBracketTemplate) {
                        isBracketTemplate = false;
                    }
                    this.currentToken = RobotStubTokenTypes.KEYWORD_DEFINITION;
                    this.level.push(KEYWORD_DEFINITION);
                } else if (state == KEYWORD_DEFINITION && isAssignment(this.position)) {
                    this.position++;
                    this.currentToken = RobotTokenTypes.WHITESPACE;
                } else {
                    if (!this.isTestTemplate && !this.isBracketTemplate || parentState != TEST_CASES_HEADING && state != KEYWORD_DEFINITION) {
                        this.currentToken = RobotTokenTypes.KEYWORD;
                        this.level.push(KEYWORD);
                        if (!isSuperSpaceOrNewline(this.position)) {
                            this.level.push(KEYWORD);
                        }
                    } else {
                        if (isAssignment(this.position + 1)) {
                            this.currentToken = RobotTokenTypes.PARAMETER;
                        } else {
                            this.currentToken = RobotStubTokenTypes.ARGUMENT;
                        }
                        this.level.push(KEYWORD);
                    }
                }
            }
        }
    }

    private void handleSyntax() {
        if (isSuperSpace(this.position)) {
            skipWhitespace();
            this.currentToken = RobotTokenTypes.WHITESPACE;
        } else {
            goToNextNewLineOrSuperSpace();
            this.level.push(KEYWORD);
            this.currentToken = RobotTokenTypes.KEYWORD;
        }
    }

    private void handleGherkin() {
        this.level.pop();
        this.currentToken = RobotTokenTypes.WHITESPACE;
        this.position++;
    }

    private void handleRemainingStates(int state, int parentState) {
        if (isSuperSpace(this.position)) {
            skipWhitespace();
            this.currentToken = RobotTokenTypes.WHITESPACE;
            if (state == KEYWORD && parentState == KEYWORD) {
                this.level.pop();
            }
        } else if (isEllipsis(this.position)) {
            if (isOnlyWhitespaceToPreviousLine(position - 1)) {
                goToNextNewLineOrSuperSpace();
                this.currentToken = RobotTokenTypes.WHITESPACE;
            } else {
                goToNextNewLineOrSuperSpace();
                this.currentToken = RobotStubTokenTypes.ARGUMENT;
            }
        } else if (state == VARIABLE_DEFINITION && isVariableEnd(this.position - 1)) {
            goToNextNewLineOrSuperSpace();
            this.currentToken = RobotTokenTypes.WHITESPACE;
        } else if (state == VARIABLE_DEFINITION && getState() == KEYWORD_DEFINITION) {
            handleVariableDefinitionInKeyword();
        } else if (isVariable(this.position)) {
            goToVariableEnd();
            if (state == SETTING && isSuperSpacePrevious()) {
                this.currentToken = RobotStubTokenTypes.VARIABLE_DEFINITION;
            } else if (!isWithinForLoop() && !isVarOrAsTokenPresent(this.position)) {
                this.currentToken = RobotTokenTypes.VARIABLE;
            } else {
                this.currentToken = RobotStubTokenTypes.VARIABLE_DEFINITION;
                this.level.push(VARIABLE_DEFINITION);
            }
        } else if (state == KEYWORD && parentState == KEYWORD) {
            goToNextNewLineOrSuperSpaceOrVariable();
            this.currentToken = RobotTokenTypes.KEYWORD;
        } else {
            goToNextNewLineOrSuperSpaceOrVariableOrArgument();
            if (charAtEquals(this.position, '=') && charAtEquals(this.position + 1, '=')) {
                goToNextNewLineOrSuperSpaceOrVariable();
            }
            String word = getTokenText();
            if (word.isEmpty() && isAssignment(this.position)) {
                this.position++;
                this.currentToken = RobotTokenTypes.WHITESPACE;
            } else if (this.startOffset == 0 || charAtEquals(this.startOffset - 1, '\n')) {
                this.currentToken = RobotStubTokenTypes.KEYWORD_DEFINITION;
                this.isBracketTemplate = false;
            } else if (RobotKeywordProvider.isSyntaxOfType(RobotTokenTypes.SYNTAX_MARKER, word)) {
                this.currentToken = RobotTokenTypes.SYNTAX_MARKER;
                this.level.push(IF_CLAUSE);
            } else if (VARIABLE_DEFINITION == state && KEYWORD_DEFINITION == parentState) {
                this.level.push(KEYWORD);
                this.currentToken = RobotTokenTypes.KEYWORD;
            } else if (parentState != IMPORT || state != IF_CLAUSE) {
                if (isAssignment(this.position)) {
                    this.currentToken = RobotTokenTypes.PARAMETER;
                } else {
                    this.currentToken = RobotStubTokenTypes.ARGUMENT;
                }
            } else {
                this.currentToken = RobotStubTokenTypes.VARIABLE_DEFINITION;
            }
        }
    }

    private void handleVariableDefinitionInKeyword() {
        goToNextNewLineOrSuperSpace();
        if ("IF".equals(getTokenText())) {
            this.currentToken = RobotTokenTypes.SYNTAX_MARKER;
            this.level.push(IF_CLAUSE);
        } else {
            this.currentToken = RobotTokenTypes.KEYWORD;
            this.level.push(KEYWORD);
        }
    }

    private boolean isWithinForLoop() {
        int startOffset = this.startOffset;
        List<TokenWord> tokenWords = extractTokenWords();
        if (!tokenWords.isEmpty() && "FOR".equals(tokenWords.getFirst().word)) {
            TokenWord forToken = tokenWords.getFirst();
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
        List<TokenWord> tokenWords = extractTokenWords();
        int tokenCount = tokenWords.size();
        for (int i = 0; i < tokenCount; i++) {
            TokenWord token = tokenWords.get(i);
            if (("VAR".equals(token.word) || "AS".equals(token.word)) && i + 1 < tokenCount && tokenWords.get(i + 1).start == startOffset) {
                return true;
            }
        }
        return false;
    }

    private boolean isVariable(int position) {
        if (isVariableStart(position)) {
            position += 2;
            for (int i = 1; i > 0 && position < this.endOffset && position >= 0; position++) {
                if (charAtEquals(position, '}')) {
                    if (--i == 0) {
                        return true;
                    }
                }
                if (isVariableStart(position)) {
                    i++;
                    position += 2;
                }
                if (isSuperSpaceOrNewline(position)) {
                    return false;
                }
            }
        }
        return false;
    }

    private boolean isComment(int position) {
        while (position < this.endOffset && (isWhitespace(position) || isNewLine(position))) {
            position++;
        }
        return charAtEquals(position, '#');
    }

    private boolean isVariableStart(int position) {
        return (charAtEquals(position, '$') || charAtEquals(position, '@') || charAtEquals(position, '&') || charAtEquals(position, '%')) &&
               charAtEquals(position + 1, '{');
    }

    private List<TokenWord> extractTokenWords() {
        List<TokenWord> tokenWords = new ArrayList<>();
        int start = this.startOffset;
        while (start > 0 && !charAtEquals(start - 1, '\n')) {
            start--;
        }
        int end = start;
        while (end < this.endOffset && !charAtEquals(end, '\n')) {
            end++;
        }
        while (start < end) {
            while (start < end && Character.isWhitespace(this.buffer.charAt(start))) {
                start++;
            }
            int wordEnd = start;
            while (wordEnd < end && !isSuperSpaceOrNewline(wordEnd)) {
                wordEnd++;
            }
            String word = this.buffer.subSequence(start, wordEnd).toString();
            if (!word.isEmpty()) {
                tokenWords.add(new TokenWord(word, start, wordEnd));
            }
            start = wordEnd + 1;
        }
        return tokenWords;
    }

    private static boolean isSettings(String line) {
        return "*** Settings ***".equalsIgnoreCase(line) || "*** Setting ***".equalsIgnoreCase(line);
    }

    private static boolean isTestCases(String line) {
        return line.toLowerCase().contains("*** test cases ***") || line.toLowerCase().contains("*** test case ***");
    }

    private static boolean isKeywords(String line) {
        return "*** Keywords ***".equalsIgnoreCase(line) || "*** Keyword ***".equalsIgnoreCase(line);
    }

    private static boolean isVariables(String line) {
        return "*** Variables ***".equalsIgnoreCase(line) || "*** Variable ***".equalsIgnoreCase(line);
    }

    private static boolean isTasks(String line) {
        return "*** Tasks ***".equalsIgnoreCase(line) || "*** Task ***".equalsIgnoreCase(line);
    }

    private static boolean isComments(String line) {
        return "*** Comments ***".equalsIgnoreCase(line) || "*** Comment ***".equalsIgnoreCase(line);
    }

    private boolean isHeading(int position) {
        return charAtEquals(position, '*') && charAtEquals(position + 1, '*') && charAtEquals(position + 2, '*') && isSpace(position + 3);
    }

    private boolean isEllipsis(int position) {
        while (this.position < this.endOffset && (isWhitespace(position) || isNewLine(position))) {
            position++;
        }
        return charAtEquals(position, '.') && charAtEquals(position + 1, '.') && charAtEquals(position + 2, '.') && isSuperSpaceOrNewline(position + 3);
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
        return isSpace(position) && isSpace(position + 1) || isSpace(position) && isTab(position + 1) || isTab(position);
    }

    private boolean isTab(int position) {
        return charAtEquals(position, '\t');
    }

    private boolean isSpace(int position) {
        return charAtEquals(position, ' ');
    }

    private boolean isSuperSpaceOrNewline(int position) {
        return isSuperSpace(position) || isNewLine(position);
    }

    private boolean isVariableDefinition(int position) {
        return isSuperSpaceOrNewline(position) ||
               charAtEquals(position, '=') && isSuperSpaceOrNewline(position + 1) ||
               isSpace(position) && charAtEquals(position + 1, '=') && isSuperSpaceOrNewline(position + 2);
    }

    private void goToEndOfLine() {
        while (this.position < this.endOffset && !isNewLine(this.position)) {
            this.position++;
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
        while (this.position < this.endOffset && !isSuperSpaceOrNewline(this.position)) {
            this.position++;
        }
    }

    private void goToNextNewLineOrSuperSpaceOrVariable() {
        while (this.position < this.endOffset && !isSuperSpaceOrNewline(this.position) && !isVariable(this.position)) {
            this.position++;
        }
    }

    private void goToNextNewLineOrSuperSpaceOrVariableOrArgument() {
        while (this.position < this.endOffset && !isSuperSpaceOrNewline(this.position) && !isVariable(this.position) && !isAssignment(this.position)) {
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

    private boolean isAssignment(int position) {
        return charAtEquals(position, '=') && !charAtEquals(position + 1, '=');
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
