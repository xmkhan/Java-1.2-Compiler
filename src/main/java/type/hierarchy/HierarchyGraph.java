package type.hierarchy;

import exception.TypeHierarchyException;
import token.ImportDeclaration;

import java.util.*;

/**
 * Represent the hierarchy graph
 */
public class HierarchyGraph {
  private HashMap<String, HierarchyGraphNode> nodes;

  public void reset() {
    this.nodes.clear();
  }

  public HierarchyGraph() {
    this.nodes = new HashMap<String, HierarchyGraphNode>();
  }

  /**
   * Adds a node to the graph if it does not exist.
   * Throws an exception if it does
   */
  public HierarchyGraphNode createNode(String name) throws TypeHierarchyException {
    if (nodes.containsKey(name)) {
      throw new TypeHierarchyException("Node named " + name + " already exists in hierarchy graph");
    }
    HierarchyGraphNode node;
    node = new HierarchyGraphNode();
    node.identifier = name;
    nodes.put(name, node);
    return node;
  }

  /**
   * Returns the node corresponding to name
   * Creates a new node if it doesn't exist
   */
  public HierarchyGraphNode getNode(HierarchyGraphNode node, String name, List<ImportDeclaration> imports) throws TypeHierarchyException {
    if (nodes.containsKey(name)) {
      return nodes.get(name);
    } else if (nodes.containsKey(node.getPackageName() + name)) {
      return nodes.get(node.getPackageName() + name);
    } else if (imports.size() > 0) {
      // Check to see if a node with identifier import.class name exists
      for (ImportDeclaration imported : imports) {
        String importName = imported.getLexeme() + (imported.onDemand ? "." + name : "");
        if (nodes.containsKey(importName)) {
          return nodes.get(importName);
        }
      }
    } else if (nodes.containsKey("java.lang." + name)) {
      return nodes.get("java.lang." + name);
    }
    throw new TypeHierarchyException(name + " was not found in hierarchy graph");
  }

  public HierarchyGraphNode get(String name) {
    return nodes.get(name);
  }

  public boolean contains(String name) {
    return nodes.containsKey(name);
  }

  public Set<Map.Entry<String, HierarchyGraphNode>> entrySet() {
    return nodes.entrySet();
  }
}
