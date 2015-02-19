package type.hierarchy;

import exception.DeadCodeException;
import exception.TypeHierarchyException;
import token.*;

import java.util.List;
import java.util.Map;

/**
 * Responsible for performing Class Hierarchy checks
 */
public class HierarchyChecker {
  private HierarchyGraph graph;

  public HierarchyChecker() {
    this.graph = new HierarchyGraph();
  }

  public void reset() {
    this.graph.nodes.clear();
  }

  public void verifyClassAndInterfaceHierarchy(List<CompilationUnit> compilationUnits) throws TypeHierarchyException, DeadCodeException {
    createHierarchyGraph(compilationUnits);
    verifyHierarchyGraph();
  }

  /**
   * Creates a hierarchy graph of all the interfaces and classes being compiled
   * @param compilationUnits list of all the CompilationUnits (one per input file)
   * @throws TypeHierarchyException
   */
  private void createHierarchyGraph(List<CompilationUnit> compilationUnits) throws TypeHierarchyException, DeadCodeException {
    for (CompilationUnit compilationUnit : compilationUnits) {
      addNode(compilationUnit);
    }
  }

  /**
   * Adds a node to the hierarchy graph
   * @param compilationUnit Search this compilation unit for class/interface info
   * @throws TypeHierarchyException
   */
  private void addNode(CompilationUnit compilationUnit) throws TypeHierarchyException, DeadCodeException {
    Token classOrInterface = compilationUnit.children.get(compilationUnit.children.size()-1).children.get(0);
    if (classOrInterface.getTokenType().equals(TokenType.ClassDeclaration) ||
      classOrInterface.getTokenType().equals(TokenType.InterfaceDeclaration)) {
      graph.addNode(classOrInterface);
    } else {
      throw new DeadCodeException("Expecting a ClassDeclaration or InterfaceDeclaration token but received " + classOrInterface.getTokenType());
    }
  }

  /**
   * Perform all the necessary hierarchy verifications
   * Please see class comments for full detail
   * @throws TypeHierarchyException
   */
  private void verifyHierarchyGraph() throws TypeHierarchyException {
    HierarchyGraphNode parentNode;
    HierarchyGraphNode currentNode;
    String name;

    for (Map.Entry<String, HierarchyGraphNode> entry : graph.nodes.entrySet()) {
      name = entry.getKey();
      currentNode = entry.getValue();

      extendsVerification(currentNode.extendsList, currentNode);
      implementsVerification(currentNode.implementsList, name);
    }
    verifyHierarchyGraphIsAcyclic();
  }

  /*******************************************  Verification Functions *******************************************/

  private void verifyHierarchyGraphIsAcyclic() throws TypeHierarchyException {
    HierarchyGraphNode cyclicNode;
    if ((cyclicNode = graph.isCyclic()) != null) throw new TypeHierarchyException("Graph is not acyclic.  " +
      cyclicNode.identifier + " is causing cycles in hierarchy checking");
  }

  /**
   * Perform verification on the implements clause of a class
   * @param implementedParents interfaces implemented by this class
   * @param className name of the class being processed
   * @throws TypeHierarchyException
   */
  private void implementsVerification(List<HierarchyGraphNode> implementedParents, String className) throws TypeHierarchyException {
    for (HierarchyGraphNode parent : implementedParents) {
      if (parent.classOrInterface.getTokenType().equals(TokenType.ClassDeclaration)) {
        throw new TypeHierarchyException("A Class cannot implement a class [class: " + className +
          ", implemented class: " + parent.identifier + "]");
      }
    }
  }

  /**
   * Perform verification on the extends clause of a class or interface
   * @param parents parents of the class being processed
   * @param currentNode HierarchyGraph node associated to the class/interface being processed
   * @throws TypeHierarchyException
   */
  public void extendsVerification(List<HierarchyGraphNode> parents, HierarchyGraphNode currentNode) throws TypeHierarchyException {
    for (HierarchyGraphNode parent : parents) {
      if (currentNode.classOrInterface instanceof ClassDeclaration) {
        if (parent.classOrInterface instanceof InterfaceDeclaration) {
          throw new TypeHierarchyException("A class cannot extend an interface[class: " + currentNode.identifier +
            ", interface: " + parent.identifier + "]");
        }
        if (parent.isFinal()) {
          throw new TypeHierarchyException("Class " + currentNode.identifier + " is extending final class " + parent.identifier);
        }
      } else if (currentNode.classOrInterface instanceof InterfaceDeclaration &&
        parent.classOrInterface instanceof ClassDeclaration) {
        throw new TypeHierarchyException("An interface cannot extend a class[Interface " + currentNode.identifier +
          ", class:" + parent.identifier + "]");
      }
    }
  }

  private void verifyGraphIsAcyclic(HierarchyGraphNode currentNode) {

  }
}
