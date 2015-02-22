package symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

  public void add(K key, V value) {
    if (symbols.containsKey(key)) {
      symbols.get(key).add(value);
    } else {
      symbols.put(key, new ArrayList<V>());
      symbols.get(key).add(value);
    }
  }

  public void remove(K key) {
    symbols.remove(key);
  }

  public void remove(K key, V value) {
    if (symbols.containsKey(key)) {
      symbols.get(key).remove(value);
      if (symbols.get(key).isEmpty()) {
        symbols.remove(key);
      }
    }
  }

  public boolean containsPrefix(String prefix) {
    String[] prefixNames = prefix.split("\\.");
    for (K key : symbols.keySet()) {
      String[] keyNames = key.toString().split("\\.");
      if (keyNames.length < prefixNames.length) continue;
      boolean matched = true;
      for (int i = 0; i < prefixNames.length; ++i) {
        if (!prefixNames[i].equals(keyNames[i])) {
          matched = false;
          break;
        }
      }
      if (matched) return true;
    }
    return false;
  }

  public List<V> findWithPrefix(String prefix) {
    String[] prefixNames = prefix.split("\\.");
    List<V> values = new ArrayList<V>();
    for (K key : symbols.keySet()) {
      String[] keyNames = key.toString().split("\\.");
      if (keyNames.length < prefixNames.length) continue;
      boolean matched = true;
      for (int i = 0; i < prefixNames.length; ++i) {
        if (!prefixNames[i].equals(keyNames[i])) {
          matched = false;
          break;
        }
      }
      if (matched) values.addAll(symbols.get(key));
    }
    return values;
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
