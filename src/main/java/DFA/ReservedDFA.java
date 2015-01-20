package dfa;

import algorithm.trie.Trie;
import token.Token;
import token.TokenType;

import java.util.HashMap;

/**
 *
 */
public class ReservedDFA implements DFA {
  private Token token;

  private Trie trie;
  private Trie.Path path;

  HashMap<String, TokenType> reverseTokenTypeMap;

  public ReservedDFA() {
    trie = new Trie();
    reverseTokenTypeMap = new HashMap<>(TokenType.RESERVED_LENGTH.ordinal());
    addReservedKeywords();
    reset();
  }

  private void addReservedKeywords() {
    for (int i = 0; i < TokenType.RESERVED_LENGTH.ordinal(); ++i) {
      TokenType token = TokenType.values()[i];
      trie.insert(token.name());
      reverseTokenTypeMap.put(token.name(), token);
    }
  }

  @Override
  public void reset() {
    token = null;
    path = trie.constructPath();
  }

  @Override
  public boolean consume(char c) {
    path = trie.traverse(path, c);
    if (path.getState() == Trie.Path.states.WORD) {
      String word = path.toString();
      token = new Token(word, reverseTokenTypeMap.get(word));
    }
    return path.getState() != Trie.Path.states.ERROR;
  }

  @Override
  public Token getToken() {
    return token;
  }
}
