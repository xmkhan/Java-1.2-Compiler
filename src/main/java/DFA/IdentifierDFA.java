package dfa;

import token.Token;

/**
 *
 */
public class IdentifierDFA implements DFA {
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
