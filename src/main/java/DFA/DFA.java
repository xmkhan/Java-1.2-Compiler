package dfa;

import token.Token;

/**
 * Interface for implementing a DFA.
 */
public interface DFA {

  /**
   * Resets the DFA to the starting state.
   */
  void reset();

  /**
   * Consumes the input character.
   * @param c character to consume.
   * @return true if c is successfully consumed, false if error state
   */
  boolean consume(char c);

  /**
   * Returns the largest valid token known to the DFA (ie. last accepting state).
   */
  Token getToken();
}
