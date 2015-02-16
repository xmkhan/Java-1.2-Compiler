package type.hierarchy;

import token.Token;

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
  public ClassNode parent;
}
