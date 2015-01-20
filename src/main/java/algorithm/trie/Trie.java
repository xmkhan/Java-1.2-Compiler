package algorithm.trie;

/**
 * Implementation of a trie data structure, supports 8-bit ASCII
 */
public class Trie {
  private static final int ASCII_SIZE = 128;

  private class node {
    public node[] nodes = new node[ASCII_SIZE];
    boolean isWord = false;
  }


}
