package algorithm.trie;

/**
 * Implementation of a trie data structure, supports 8-bit ASCII
 */
public class Trie {
  private static final int ASCII_SIZE = 128;
  private node head;

  public Trie() {
    head = new node();
  }

  public void insert(String word) {
    node traverse = head;
    for (int i = 0; i < word.length(); ++i) {
      char c = word.charAt(i);
      if (traverse.children[c] == null) {
        traverse.children[c] = new node();
      }
      traverse = traverse.children[c];
    }
    traverse.isWord = true;
  }

  public void remove(String word) {
    node traverse = head;
    for (int i = 0; i < word.length(); ++i) {
      char c = word.charAt(i);
      traverse = traverse.children[c];
    }
    traverse.isWord = false;
  }

  public boolean contains(String word) {
    node traverse = head;
    for (int i = 0; i < word.length(); ++i) {
      char c = word.charAt(i);
      if (traverse.children[c] == null) return false;
      traverse = traverse.children[c];
    }
    return traverse.isWord;
  }

  private class node {
    boolean isWord = false;
    public node[] children = new node[ASCII_SIZE];
  }


}
