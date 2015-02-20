package symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * A class that encapsulates a scope that uses a balanced-tree to achieve O(logn) lookup.
 * We assume that scopes typically have a small set of variable declarations.
 * Thus, a TreeMap provides better space and runtime complexity trade-offs than a HashMap.
 */
public class Scope<K, V> {
  private TreeMap<K, List<V>> symbols;

  public Scope() {
    this.symbols = new TreeMap<K, List<V>>();
  }

  public Scope add(K key, V value) {
    if (symbols.containsKey(key)) {
      symbols.get(key).add(value);
    } else {
      symbols.put(key, new ArrayList<V>());
      symbols.get(key).add(value);
    }
    return this;
  }

  public Scope remove(K key) {
    symbols.remove(key);
    return this;
  }

  public Scope remove(K key, V value) {
    if (symbols.containsKey(key)) {
      symbols.get(key).remove(value);
      if (symbols.get(key).isEmpty()) {
        symbols.remove(key);
      }
    }
    return this;
  }

  public int size() {
    return symbols.size();
  }

  public List<V> find(K key) {
    return symbols.get(key);
  }

  public boolean contains(K key) {
    return symbols.containsKey(key);
  }
}
