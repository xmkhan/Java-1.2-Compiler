package type.hierarchy;

import exception.TypeHierarchyException;
import token.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Responsible for performing Class Hierarchy checks including
 *
 * A class must not extend an interface. (JLS 8.1.3, dOvs simple constraint 1)
 * A class must not implement a class. (JLS 8.1.4, dOvs simple constraint 2)
 * An interface must not be repeated in an implements clause, or in an extends clause of an interface. (JLS 8.1.4, dOvs simple constraint 3)
 * A class must not extend a final class. (JLS 8.1.1.2, 8.1.3, dOvs simple constraint 4)
 * An interface must not extend a class. (JLS 9.1.2)
 * The hierarchy must be acyclic. (JLS 8.1.3, 9.1.2, dOvs well-formedness constraint 1)
 * A class or interface must not declare two methods with the same signature (name and parameter types). (JLS 8.4, 9.4, dOvs well-formedness constraint 2)
 * A class must not declare two constructors with the same parameter types (dOvs 8.8.2, simple constraint 5)
 * A class or interface must not contain (declare or inherit) two methods with the same signature but different return types (JLS 8.1.1.1, 8.4, 8.4.2, 8.4.6.3, 8.4.6.4, 9.2, 9.4.1, dOvs well-formedness constraint 3)
 * A class that contains (declares or inherits) any abstract methods must be abstract. (JLS 8.1.1.1, well-formedness constraint 4)
 * A nonstatic method must not replace a static method (JLS 8.4.6.1, dOvs well-formedness constraint 5)
 * A method must not replace a method with a different return type. (JLS 8.1.1.1, 8.4, 8.4.2, 8.4.6.3, 8.4.6.4, 9.2, 9.4.1, dOvs well-formedness constraint 6)
 * A protected method must not replace a public method. (JLS 8.4.6.3, dOvs well-formedness constraint 7)
 * A method must not replace a final method. (JLS 8.4.3.3, dOvs well-formedness constraint 9)
 */
public class ClassHierarchy {
  private ClassHierarchyGraph graph;

  public ClassHierarchy() {
    this.graph = new ClassHierarchyGraph();
  }

  private void processCompilationUnit(CompilationUnit compilationUnit) throws TypeHierarchyException {
    Token classOrInterface = compilationUnit.children.get(compilationUnit.children.size()-1).children.get(0);
    if (classOrInterface.getTokenType().equals(TokenType.ClassDeclaration)) {
       graph.addClass((ClassDeclaration) classOrInterface);
    } else if (classOrInterface.getTokenType().equals(TokenType.InterfaceDeclaration)) {
      graph.addInterface((InterfaceDeclaration) classOrInterface);
    } else {
      throw new TypeHierarchyException("Invalid class or interface declaration:" + classOrInterface.getTokenType());
    }

  }

  public void processCompilationUnits(List<CompilationUnit> compilationUnits) throws TypeHierarchyException {
    for (CompilationUnit compilationUnit : compilationUnits) {
      processCompilationUnit(compilationUnit);
    }
  }

  public void verifyClassHierarchy() throws TypeHierarchyException {
    ClassNode parentNode;
    ClassNode currentNode;
    String name;
    Iterator it = graph.graph.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry pairs = (Map.Entry)it.next();
      name = (String) pairs.getKey();
      currentNode = graph.graph.get(name);
      parentNode = (ClassNode) pairs.getValue();

      extendsVerification(parentNode.extendsList, currentNode);
      implementsVerification(parentNode.implementsList, name);
    }
  }

  private void implementsVerification(List<ClassNode> parents, String className) throws TypeHierarchyException {
    HashSet<String> parentNames = new HashSet<>();
    for (ClassNode parent : parents) {
      if (parent.classOrInterface.getTokenType().equals(TokenType.ClassDeclaration)) {
        throw new TypeHierarchyException("You cannot implement a class[class: " + className +
          ", implemented class: " + parent.identifier + "]");
      }
      if (parentNames.contains(parent.identifier)) {
        throw new TypeHierarchyException(parent.identifier + " is repeated in the extends clause of " + className);
      }
      parentNames.add(parent.identifier);
    }
  }

  public void extendsVerification(List<ClassNode> parents, ClassNode currentNode) throws TypeHierarchyException {
    HashSet<String> parentNames = new HashSet<>();
    for (ClassNode parent : parents) {
      if (currentNode.classOrInterface.getTokenType().equals(TokenType.ClassDeclaration) &&
        parent.classOrInterface.getTokenType().equals(TokenType.InterfaceType)) {
        throw new TypeHierarchyException("You cannot extend an interface[class: " + currentNode.identifier +
          ", interface name: " + parent.identifier + "]");
      }
      if (currentNode.classOrInterface.getTokenType().equals(TokenType.InterfaceType) &&
        parent.classOrInterface.getTokenType().equals(TokenType.ClassDeclaration)) {
        throw new TypeHierarchyException("Interface " + currentNode.identifier +
          "is attempting to extend class " + parent.identifier);
      }
      if (parentNames.contains(parent.identifier)) {
        throw new TypeHierarchyException(parent.identifier + " is repeated in the implements clause of " + currentNode.identifier);
      }
      if (parent.isFinal()) {
        throw new TypeHierarchyException("Class " + currentNode.identifier + " is extending final class " + parent.identifier);
      }
      parentNames.add(parent.identifier);
    }
  }
}
