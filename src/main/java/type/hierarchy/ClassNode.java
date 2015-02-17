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
  // Pointer to the parent to allow for answering complete hierarchy (base class to descendant class) queries.
  public List<ClassNode> extendsList;

  public List<ClassNode> implementsList;

  public HashSet<Modifier> modifiers;

  public String className;

  public String identifier;

  public ClassNode() {
    children = new ArrayList<>();
    extendsList = new ArrayList<>();
    implementsList = new ArrayList<>();
  }

  public boolean isFinal() {
    return modifiers.contains(TokenType.FINAL);
  }
}
