package dfa;

import algorithm.trie.Trie;
import token.Token;
import token.TokenType;

import java.util.HashMap;

/**
 * Handles parsing of Reserved keywords defined in TokenType.
 * <p/>
 * Requirements:
 * Any string that matches a reserved keyword.
 * Tokens from ordinal: 0...TokenType.RESERVED_LENGTH - 1
 */
public class ReservedDFA implements DFA {
  private Token token;

  private Trie trie;
  private Trie.Path path;

  HashMap<String, TokenType> reverseTokenTypeMap;

  public ReservedDFA() {
    trie = new Trie();
    reverseTokenTypeMap = new HashMap<String, TokenType>(TokenType.RESERVED_LENGTH.ordinal());
    addReservedKeywords();
    reset();
  }

  private void addReservedKeywords() {
    for (int i = 0; i < TokenType.RESERVED_LENGTH.ordinal(); ++i) {
      TokenType token = TokenType.values()[i];
      trie.insert(token.toString());
      reverseTokenTypeMap.put(token.toString(), token);
    }
  }

  @Override
  public void reset() {
    token = null;
    path = trie.constructPath();
  }

  @Override
  public boolean consume(char c) {
    if (path.getState() == Trie.Path.states.ERROR) {
      return false;
    }

    trie.traverse(path, c);
    if (path.getState() == Trie.Path.states.WORD) {
      String word = path.toString();
      token = new Token(word, reverseTokenTypeMap.get(word), null);
    }
    return path.getState() != Trie.Path.states.ERROR;
  }

  @Override
  public Token getToken() {
    return token;
  }
}
