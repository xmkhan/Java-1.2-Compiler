package dfa;

import token.Token;
import token.TokenType;

/**
 * Handles parsing of identifier
 * <p/>
 * Requirements:
 * Starts with Java letter which is $, _, a - z, A - Z
 * The rest of the characters are Java letters or digits.
 */
public class IdentifierDFA implements DFA {
  private StringBuilder builder;
  private Token token;

  private enum states {ERROR, START, ACCEPT}

  ;
  private states state;

  public IdentifierDFA() {
    reset();
  }

  @Override
  public void reset() {
    state = states.START;
    token = null;
    builder = new StringBuilder();
  }

  // Checks if it is a Java letter which is $, _, a - z, A - Z
  private boolean isJavaLetter(char c) {
    return c == '_' || c == '$' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
  }

  @Override
  public boolean consume(char c) {
    switch (state) {
      case ERROR:
        break;
      case START:
        if (isJavaLetter(c)) {
          state = states.ACCEPT;
          builder.append(c);
        } else state = states.ERROR;
        break;
      case ACCEPT:
        if (isJavaLetter(c) || (c >= '0' && c <= '9')) {
          builder.append(c);
        } else {
          state = states.ERROR;
          token = new Token(builder.toString(), TokenType.IDENTIFIER);
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
