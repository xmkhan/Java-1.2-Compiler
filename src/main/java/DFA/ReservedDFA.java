package dfa;

import algorithm.trie.Trie;
import token.Token;

/**
 *
 */
public class ReservedDFA implements DFA {
  private Trie trie;

  public ReservedDFA() {
    trie = new Trie();
  }

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
