package type.hierarchy;

import exception.TypeHierarchyException;
import token.CompilationUnit;
import token.Token;
import token.TokenType;

import java.util.List;
import java.util.Map;

/**
 * Responsible for performing Class Hierarchy checks including
 * - A class must not extend an interface. (JLS 8.1.3, dOvs simple constraint 1)
 * - A class must not implement a class. (JLS 8.1.4, dOvs simple constraint 2)
 * - An interface must not be repeated in an implements clause, or in an extends clause of an interface. (JLS 8.1.4, dOvs simple constraint 3)
 * - A class must not extend a final class. (JLS 8.1.1.2, 8.1.3, dOvs simple constraint 4)
 * - An interface must not extend a class. (JLS 9.1.2)
 * - The hierarchy must be acyclic. (JLS 8.1.3, 9.1.2, dOvs well-formedness constraint 1)
 * - A class or interface must not declare two methods with the same signature (name and parameter types). (JLS 8.4, 9.4, dOvs well-formedness constraint 2)
 * - A class must not declare two constructors with the same parameter types (dOvs 8.8.2, simple constraint 5)
 * - A class or interface must not contain (declare or inherit) two methods with the same signature but different return types (JLS 8.1.1.1, 8.4, 8.4.2, 8.4.6.3, 8.4.6.4, 9.2, 9.4.1, dOvs well-formedness constraint 3)
 * - A class that contains (declares or inherits) any abstract methods must be abstract. (JLS 8.1.1.1, well-formedness constraint 4)
 * - A nonstatic method must not replace a static method (JLS 8.4.6.1, dOvs well-formedness constraint 5)
 * - A method must not replace a method with a different return type. (JLS 8.1.1.1, 8.4, 8.4.2, 8.4.6.3, 8.4.6.4, 9.2, 9.4.1, dOvs well-formedness constraint 6)
 * - A protected method must not replace a public method. (JLS 8.4.6.3, dOvs well-formedness constraint 7)
 * - A method must not replace a final method. (JLS 8.4.3.3, dOvs well-formedness constraint 9)
 */
public class ClassHierarchy {
  private ClassHierarchyGraph graph;

  public ClassHierarchy() {
    this.graph = new ClassHierarchyGraph();
  }

  public void reset() {
    this.graph.nodes.clear();
  }

  public void verifyClassHierarchy(List<CompilationUnit> compilationUnits) throws TypeHierarchyException {
    createClassHierarchyGraph(compilationUnits);
    verifyClassHierarchyGraph();
  }

  /**
   * Creates a hierarchy graph of all the interfaces and classes being compiled
   * @param compilationUnits list of all the compilation units (one/input file to the compiler)
   * @throws TypeHierarchyException
   */
  private void createClassHierarchyGraph(List<CompilationUnit> compilationUnits) throws TypeHierarchyException {
    for (CompilationUnit compilationUnit : compilationUnits) {
      createClassHierarchyGraph(compilationUnit);
    }
  }

  /**
   * Adds a node to the hierarchy graph
   * @param compilationUnit Search this compilation unit for class/interface info
   * @throws TypeHierarchyException
   */
  private void createClassHierarchyGraph(CompilationUnit compilationUnit) throws TypeHierarchyException {
    Token classOrInterface = compilationUnit.children.get(compilationUnit.children.size()-1).children.get(0);
    if (classOrInterface.getTokenType().equals(TokenType.ClassDeclaration) ||
      classOrInterface.getTokenType().equals(TokenType.InterfaceDeclaration)) {
      graph.addNode(classOrInterface);
    } else {
      throw new TypeHierarchyException("Invalid class or interface declaration:" + classOrInterface.getTokenType());
    }
  }

  /**
   * Perform all the necessary hierarchy verifications
   * Please see class comments for full detail
   * @throws TypeHierarchyException
   */
  private void verifyClassHierarchyGraph() throws TypeHierarchyException {
    ClassNode parentNode;
    ClassNode currentNode;
    String name;

    for (Map.Entry<String, ClassNode> entry : graph.nodes.entrySet()) {
      name = entry.getKey();
      currentNode = graph.nodes.get(name);
      parentNode = entry.getValue();

      extendsVerification(parentNode.extendsList, currentNode);
      implementsVerification(parentNode.implementsList, name);
    }
  }

  /*******************************************  Verification Functions *******************************************/

  /**
   * Perform verification on the implements clause of a class
   * @param parents parents of the class being processed
   * @param className name of the class being processed
   * @throws TypeHierarchyException
   */
  private void implementsVerification(List<ClassNode> parents, String className) throws TypeHierarchyException {
    for (ClassNode parent : parents) {
      if (parent.classOrInterface.getTokenType().equals(TokenType.ClassDeclaration)) {
        throw new TypeHierarchyException("A Class cannot implement a class, and an Interface cannot extend a Class[class: " + className +
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
  public void extendsVerification(List<ClassNode> parents, ClassNode currentNode) throws TypeHierarchyException {
    for (ClassNode parent : parents) {
      if (currentNode.classOrInterface.getTokenType().equals(TokenType.ClassDeclaration)) {
        if (parent.classOrInterface.getTokenType().equals(TokenType.InterfaceDeclaration)) {
          // A class must not extend an interface.
          throw new TypeHierarchyException("A class cannot extend an interface[class: " + currentNode.identifier +
            ", interface: " + parent.identifier + "]");
        }
        if (parent.isFinal()) {
          throw new TypeHierarchyException("Class " + currentNode.identifier + " is extending final class " + parent.identifier);
        }
      } else if (currentNode.classOrInterface.getTokenType().equals(TokenType.InterfaceDeclaration) &&
        parent.classOrInterface.getTokenType().equals(TokenType.ClassDeclaration)) {
        throw new TypeHierarchyException("An interface cannot extend a class[Interface " + currentNode.identifier +
          ", class:" + parent.identifier + "]");
      }
    }
  }
}