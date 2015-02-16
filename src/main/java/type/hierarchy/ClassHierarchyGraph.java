package type.hierarchy;

import token.ClassDeclaration;
import token.InterfaceDeclaration;

import java.util.HashMap;

/**
 * Creates a class hierarchy acyclic graph for all types known to the program.
 */
public class ClassHierarchyGraph {
  private HashMap<String, ClassNode> graph;

  public ClassHierarchyGraph() {
    this.graph = new HashMap<String, ClassNode>();
  }

  public ClassHierarchyGraph addBaseClass(ClassDeclaration baseClass) {
    return this;
  }

  public ClassHierarchyGraph addBaseInterface(InterfaceDeclaration baseInterface) {
    return this;
  }

  public ClassHierarchyGraph addChildClass(String baseClass, ClassDeclaration childClass) {
    return this;
  }

  public ClassHierarchyGraph addChildInterface(String baseInterface, InterfaceDeclaration childInterface) {
    return this;
  }
}
