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

  public boolean nodeAIsParentOfNodeB(HierarchyGraphNode a, HierarchyGraphNode b) {
    if (a.children.size() == 0) return false;
    if (a.children.contains(b)) {
      return true;
    }

    for (HierarchyGraphNode child : a.children) {
      if(nodeAIsParentOfNodeB(child, b) == true) {
        return true;
      }
    }
    return false;
  }

  public boolean areNodesConnected(String nodeName1, String nodeName2) throws TypeHierarchyException {
    if (!contains(nodeName1)) {
      throw new TypeHierarchyException("Node named " + nodeName1 + " does not exist in the graph");
    } else if (!contains(nodeName2)) {
      throw new TypeHierarchyException("Node named " + nodeName2 + " does not exist in the graph");
    }
    return nodeAIsParentOfNodeB(get(nodeName1), get(nodeName2)) ||
      nodeAIsParentOfNodeB(get(nodeName2), get(nodeName1));
  }

  public boolean areNodesConnected(HierarchyGraphNode node1, HierarchyGraphNode node2) throws TypeHierarchyException {
    return areNodesConnected(node1.getFullname(), node2.getFullname());
  }
}
