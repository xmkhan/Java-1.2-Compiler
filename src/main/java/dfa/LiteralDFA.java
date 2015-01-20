package dfa;

import token.Token;

/**
 * Handles parsing of string literals
 */
public class LiteralDFA implements DFA {
  private StringBuilder builder;
  private Token token = null;

  private enum states {ERROR, START, OPEN_QUOTE ,ASCII, ZERO_TO_THREE, ONE_TO_SEVEN1, CLOSE_QUOTE, ACCEPT};
  private states state;

  @Override
  public void reset() {

  }

  @Override
  public boolean consume(char c) {
    return false;
  }

  @Override
  public Token getToken() {
    return null;
  }
}
