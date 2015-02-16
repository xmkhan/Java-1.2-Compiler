package dfa;

import token.Token;
import token.TokenType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles parsing of string literals
 */
public class LiteralDFA implements DFA {
  private StringBuilder builder;
  private Token token = null;

  private enum LiteralType {SINGLE_QUOTE, DOUBLE_QUOTE}

  private LiteralType literalType;

  private enum states {ERROR, START, LITERAL, ESCAPE, ZERO_TO_THREE, ZERO_TO_SEVEN, ACCEPT}

  private states state;

  // used for detecting single quote literals with size > 1
  private int length;

  private final int MAX_ASCII = 127;

  Set<Character> escapeCharacters;

  public LiteralDFA() {
    escapeCharacters = new HashSet<Character>(Arrays.asList('b', 't', 'n', 'f', 'r', '\'', '"', '\\'));
    reset();
  }

  @Override
  public void reset() {
    length = 0;
    token = null;
    state = states.START;
    builder = new StringBuilder();
  }

  @Override
  public boolean consume(char c) {
    // check if a single quote literal is longer than 1 character
    if (literalType == LiteralType.SINGLE_QUOTE && (length > 1 || (length == 1 && c != '\''))) {
      token = new Token(builder.toString(), TokenType.STR_LITERAL);
      state = states.ERROR;
    }

    switch (state) {
      case ERROR:
        break;
      case START:
        if (c == '\'') {
          builder.append(c);
          state = states.LITERAL;
          literalType = LiteralType.SINGLE_QUOTE;
        } else if (c == '"') {
          builder.append(c);
          state = states.LITERAL;
          literalType = LiteralType.DOUBLE_QUOTE;
        } else {
          state = states.ERROR;
        }
        break;
      case LITERAL:
        if (c == '\\') {
          builder.append(c);
          state = states.ESCAPE;
        } else if (c <= MAX_ASCII && ((literalType == LiteralType.SINGLE_QUOTE && c != '\'') ||
            (literalType == LiteralType.DOUBLE_QUOTE && c != '"'))) {
          // keep the same state
          builder.append(c);
          length++;
        } else {
          endLiteral(c);
        }
        break;
      case ESCAPE:
        if (escapeCharacters.contains(c)) {
          length++;
          builder.append(c);
          state = states.LITERAL;
        } else if (c >= '0' && c <= '3') {
          builder.append(c);
          state = states.ZERO_TO_THREE;
        } else if (c > '3' && c <= '7') {
          builder.append(c);
          state = states.ZERO_TO_SEVEN;
        } else {
          state = states.ERROR;
        }
        break;
      case ZERO_TO_THREE:
        if (c >= '0' && c <= '7') {
          builder.append(c);
          state = states.ZERO_TO_SEVEN;
        } else if (c < MAX_ASCII) {
          builder.append(c);
          // \38 is the same as \3 followed by digit 8, so they count as 2 characters.
          length += 2;
          state = states.LITERAL;
        } else {
          endLiteral(c);
        }
        break;
      case ZERO_TO_SEVEN:
        if (c >= '0' && c <= '7') {
          length++;
          builder.append(c);
          state = states.LITERAL;
        } else if (c < MAX_ASCII) {
          builder.append(c);
          // \379 is the same as \37 followed by digit 9, so they count as 2 characters.
          length += 2;
          state = states.LITERAL;
        } else {
          endLiteral(c);
        }
        break;
      case ACCEPT:
        state = states.ERROR;
        token = new Token(builder.toString(), TokenType.STR_LITERAL, null);
        break;
    }

    return state != states.ERROR;
  }

  private void endLiteral(char c) {
    if ((c == '"' && literalType == LiteralType.DOUBLE_QUOTE) ||
        (c == '\'' && literalType == LiteralType.SINGLE_QUOTE)) {
      builder.append(c);
      state = states.ACCEPT;
    } else {
      state = states.ERROR;
    }
  }

  @Override
  public Token getToken() {
    return token;
  }

}
