package algorithm.trie;

/**
 * Implementation of a trie data structure, supports 8-bit ASCII
 * Supports the concept of an on-going traversal via Path class.
 * use case: useful for parsing because it can hold internal state.
 */
public class Trie {
  private static final int ASCII_SIZE = 128;
  private Node head;

  public static class Path {
    public enum states {ERROR, ONGOING, WORD}

    private states state;
    private Node node;
    private StringBuilder builder;

    public Path(Node node) {
      this.node = node;
      state = states.ONGOING;
      builder = new StringBuilder();
    }

    public states getState() {
      return state;
    }

    @Override
    public String toString() {
      return builder.toString();
    }
  }

  public Trie() {
    head = new Node();
  }

  public void insert(String word) {
    Node traverse = head;
    for (int i = 0; i < word.length(); ++i) {
      char c = word.charAt(i);
      if (traverse.children[c] == null) {
        traverse.children[c] = new Node();
      }
      traverse = traverse.children[c];
    }
    traverse.isWord = true;
  }

  public void remove(String word) {
    Node traverse = head;
    for (int i = 0; i < word.length(); ++i) {
      char c = word.charAt(i);
      traverse = traverse.children[c];
    }
    traverse.isWord = false;
  }

  public boolean contains(String word) {
    Node traverse = head;
    for (int i = 0; i < word.length(); ++i) {
      char c = word.charAt(i);
      if (traverse.children[c] == null) return false;
      traverse = traverse.children[c];
    }
    return traverse.isWord;
  }

  public Path constructPath() {
    return new Path(head);
  }

  public Path traverse(Path p, char c) {
    if (p.node.children[c] == null) {
      p.state = Path.states.ERROR;
      return p;
    }

    if (p.node.children[c].isWord) {
      p.state = Path.states.WORD;
    } else {
      p.state = Path.states.ONGOING;
    }

    p.node = p.node.children[c];
    p.builder.append(c);
    return p;
  }

  private class Node {
    boolean isWord = false;
    public Node[] children = new Node[ASCII_SIZE];
  }


}
