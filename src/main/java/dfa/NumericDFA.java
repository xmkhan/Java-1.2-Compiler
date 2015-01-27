package dfa;

import token.Token;
import token.TokenType;

/**
 * Handles parsing of numbers.
 */
public class NumericDFA implements DFA {
  private StringBuilder builder;
  private Token token;

  private enum states {ERROR, START, ACCEPT}
  private states state;
  private boolean startsWithZero;

  public NumericDFA() {
    reset();
  }

  @Override
  public void reset() {
    state = states.START;
    startsWithZero = false;
    builder = new StringBuilder();
    token = null;
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  @Override
  public boolean consume(char c) {
    switch (state) {
      case ERROR:
        break;
      case START:
        if (isDigit(c)) {
          if (c == '0') startsWithZero = true;
          state = states.ACCEPT;
          builder.append(c);
        } else state = states.ERROR;
        break;
      case ACCEPT:
          if (isDigit(c) && !startsWithZero) {
          builder.append(c);
        } else {
          state = states.ERROR;
          if (isValidNumber()) {
            token = new Token(builder.toString(), TokenType.INT_LITERAL);
          }
        }
        break;
    }
    return state != states.ERROR;
  }

  private boolean isValidNumber() {
    try {
      Integer.parseInt(builder.toString());
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  @Override
  public Token getToken() {
    return token;
  }
}
