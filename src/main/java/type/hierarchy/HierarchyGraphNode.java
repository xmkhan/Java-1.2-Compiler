package type.hierarchy;

import token.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A wrapper that represents a Class/Interface with additional attributes used by ObjectHierarchyGraph.
 */
public class HierarchyGraphNode {
  // Internal pointer to the ASTNode.
  public Token classOrInterface;
  // List of all children.
  public List<HierarchyGraphNode> children;
  // List of classes/interfaces in the extends clause.  They point to the corresponding ClassNodes in HierarchyGraph
  public List<HierarchyGraphNode> extendsList;
  // List of interfaces in the implements clause.  They point to the corresponding ClassNodes in HierarchyGraph
  public List<HierarchyGraphNode> implementsList;
  // Modifiers for the class or interface
  public List<Modifier> modifiers;
  // All the information regarding methods of a class/interface
  public List<Method> methods;
  // Class/interface identifier
  public String identifier;

  public HierarchyGraphNode() {
    children = new ArrayList<HierarchyGraphNode>();
    extendsList = new ArrayList<HierarchyGraphNode>();
    implementsList = new ArrayList<HierarchyGraphNode>();
    modifiers = new ArrayList<Modifier>();
    methods = new ArrayList<Method>();
  }

  public boolean isFinal() {
    return modifiers.contains(TokenType.FINAL);
  }

  /**
   * Checks to see if a parent with the specified name already exists.
   */
  public boolean hasParent(String name) {
    return hasParent(name, extendsList) || hasParent(name, implementsList);
  }

  private boolean hasParent(String name, List<HierarchyGraphNode> parents) {
    for (HierarchyGraphNode node : parents) {
      if (node.identifier.equals(name)) {
        return true;
      }
    }
    return false;
  }
}
