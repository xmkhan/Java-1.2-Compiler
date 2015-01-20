package dfa;

import token.Token;
import token.TokenType;

/**
 * Handles parsing of numbers.
 */
public class NumericDFA implements DFA {
  private StringBuilder builder;
  private Token token;

  private enum states {ERROR, START, NEGATIVE, ACCEPT}
  private states state;

  public NumericDFA() {
    reset();
  }

  @Override
  public void reset() {
    state = states.START;
    builder = new StringBuilder();
    token = null;
  }

  @Override
  public boolean consume(char c) {
    switch (state) {
      case ERROR:
        break;
      case START:
        if (c >= 0 && c <= 9) {
          state = states.ACCEPT;
          builder.append(c);
        } else if (c == '-') {
          state = states.NEGATIVE;
          builder.append(c);
        } else state = states.ERROR;
        break;
      case NEGATIVE:
        if (c >= 0 && c <= 9) {
          state = states.ACCEPT;
          builder.append(c);
        } else state = states.ERROR;
      case ACCEPT:
        if (c >= 0 && c <= 9) {
          builder.append(c);
        }
        else {
          state = states.ERROR;
          token = new Token(builder.toString(), TokenType.INT_LITERAL);
        }
        break;
    }
    return state != states.ERROR;
  }

  @Override
  public Token getToken() {
    return token;
  }
}
