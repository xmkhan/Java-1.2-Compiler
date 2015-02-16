package symbol;

import java.util.TreeMap;

/**
 * A class that encapsulates a scope that uses a balanced-tree to achieve O(logn) lookup.
 * We assume that scopes typically have a small set of variable declarations.
 * Thus, a TreeMap provides better space and runtime complexity trade-offs than a HashMap.
 */
public class Scope<K, V> {
  private TreeMap<K, V> symbols;

  public Scope() {
    this.symbols = new TreeMap<K, V>();
  }

  public Scope add(K key, V value) {
    symbols.put(key, value);
    return this;
  }

  public Scope remove(K key) {
    symbols.remove(key);
    return this;
  }

  public V find(K key) {
    return symbols.get(key);
  }

  public boolean contains(K key) {
    return symbols.containsKey(key);
  }
}
