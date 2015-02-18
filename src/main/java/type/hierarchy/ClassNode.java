package type.hierarchy;

import token.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A wrapper that represents a Class/Interface with additional attributes used by ObjectHierarchyGraph.
 */
public class ClassNode {
  // Internal pointer to the ASTNode.
  public Token classOrInterface;
  // List of all children.
  public List<ClassNode> children;
  // List of classes/interfaces in the extends clause.  They point to the corresponding ClassNodes in ClassHierarchyGraph
  public List<ClassNode> extendsList;
  // List of interfaces in the implements clause.  They point to the corresponding ClassNodes in ClassHierarchyGraph
  public List<ClassNode> implementsList;
  // Modifiers for the class or interface
  public HashSet<TokenType> modifiers;
  // Class/interface identifier
  public String identifier;

  public ClassNode() {
    children = new ArrayList<>();
    extendsList = new ArrayList<>();
    implementsList = new ArrayList<>();
    modifiers = new HashSet<>();
  }

  public boolean isFinal() {
    return modifiers.contains(TokenType.FINAL);
  }

  public boolean hasParent(String name) {
    return hasParent(name, extendsList) || hasParent(name, implementsList);
  }

  private boolean hasParent(String name, List<ClassNode> parents) {
    for (ClassNode classNode : parents) {
      if (classNode.identifier.equals(name)) {
        return true;
      }
    }
    return false;
  }
}
