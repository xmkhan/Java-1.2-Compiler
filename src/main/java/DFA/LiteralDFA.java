package dfa;

import token.Token;
import token.TokenType;

/**
 * Handles parsing of string literals
 */
public class LiteralDFA implements DFA {
  private StringBuilder builder;
  private Token token = null;

  private enum LiteralType {SINGLE_QUOTE, DOUBLE_QUOTE};
  private LiteralType literalType;

  private enum states {ERROR, START, LITERAL, ESCAPE, ZERO_TO_THREE, ZERO_TO_SEVEN1,
    ZERO_TO_SEVEN2, ACCEPT};
  private states state;

  private int length;

  private final int MAX_ASCII = 127;

  @Override
  public void reset() {
    length = 0;
    state = states.START;
    builder = new StringBuilder();
    token = null;
  }

  @Override
  public boolean consume(char c) {
    if (literalType == LiteralType.SINGLE_QUOTE && length > 1 && c == '\'') {
      state = states.ERROR;
    }

    switch(state) {
      case ERROR:
        break;
      case START:
        if (c == '\'') {
          state = states.LITERAL;
          literalType = LiteralType.SINGLE_QUOTE;
        } else if (c == '"') {
          state  = states.LITERAL;
          literalType = LiteralType.DOUBLE_QUOTE;
        } else {
          state = states.ERROR;
        }
        break;
      case LITERAL:
        if (c == '\\') {
          state = states.ESCAPE;
        } else if (c <= MAX_ASCII) {
          // keep the same states
        } else {
          endLiteral(c);
        }
        break;
      case ESCAPE:
        if (c == 'b' || c == 't' || c == 'r') {
          state = states.LITERAL;
        } else if (c >= '0' && c <= '3') {
          state = states.ZERO_TO_THREE;
        } else if (c > '3' && c <= '7') {
          state = states.ZERO_TO_SEVEN2;
        } else {
          state = states.ERROR;
        }
        break;
      case ZERO_TO_THREE:
        if (c >= '0' && c <= '7') {
          state = states.ZERO_TO_SEVEN2;
        } else {
          endLiteral(c);
        }
        break;
      case ZERO_TO_SEVEN1:
        if (c >= '0' && c <= '7') {
          state = states.ZERO_TO_SEVEN2;
        } else {
          endLiteral(c);
        }
        break;
      case ZERO_TO_SEVEN2:
        if (c >= '0' && c <= '7') {
          state = states.LITERAL;
        } else {
          endLiteral(c);
        }
        break;
      case ACCEPT:
        state = states.ERROR;
        token = new Token(builder.toString(), TokenType.INT_LITERAL);
        break;
    }

    if (c != '"' && c != '\'' && state != states.ERROR) {
      builder.append(c);
      length++;
    }

    return state != states.ERROR;
  }

  private void endLiteral(char c) {
    if (c == '"' && literalType == LiteralType.DOUBLE_QUOTE ||
            c == '\'' && literalType == LiteralType.SINGLE_QUOTE) {
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
