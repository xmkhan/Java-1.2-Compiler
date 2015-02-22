package type.hierarchy;

import exception.DeadCodeException;
import exception.TypeHierarchyException;
import token.*;

import java.util.*;

/**
 * Responsible for performing Class Hierarchy checks
 */
public class HierarchyChecker {
  private String OBJECT_CLASS = "java.lang.Object";
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
    Map<CompilationUnit, HierarchyGraphNode> compilationUnitToNode = new HashMap<CompilationUnit, HierarchyGraphNode>();
    // Construct the hierarchy graph with only the nodes representing the files
    for (CompilationUnit compilationUnit : compilationUnits) {
      compilationUnitToNode.put(compilationUnit, graph.createNode(compilationUnit));
    }

    // Fill in each node and add node relationships
    for (CompilationUnit compilationUnit : compilationUnits) {
      graph.processNode(compilationUnit, compilationUnitToNode.get(compilationUnit));
    }
  }

  /**
   * Perform all the necessary hierarchy verifications
   * Please see class comments for full detail
   * @throws TypeHierarchyException
   */
  private void verifyHierarchyGraph() throws TypeHierarchyException, DeadCodeException {
    HierarchyGraphNode currentNode;

    for (Map.Entry<String, HierarchyGraphNode> entry : graph.nodes.entrySet()) {
      currentNode = entry.getValue();
      verifyExtendedClassOrInterfaces(currentNode.extendsList, currentNode);
      verifyImplementedInterfaces(currentNode.implementsList, currentNode);
      checkForConstructorDuplication(currentNode);
    }
    isCyclic();
    verifyMethodHierarchy();
  }

  /*******************************************  Verification Functions *******************************************/

  /**
   * Perform verification on the extends clause of a class or interface
   * @param parents parents of the class being processed
   * @param currentNode HierarchyGraph node associated to the class/interface being processed
   * @throws TypeHierarchyException
   */
  private void verifyExtendedClassOrInterfaces(List<HierarchyGraphNode> parents, HierarchyGraphNode currentNode) throws TypeHierarchyException {
    for (HierarchyGraphNode parent : parents) {
      if (currentNode.classOrInterface instanceof ClassDeclaration) {
        if (parent.classOrInterface instanceof InterfaceDeclaration) {
          throw new TypeHierarchyException("A class cannot extend an interface[class: " +
            currentNode.identifier + ", interface: " + parent.identifier + "]");
        }
        if (parent.isFinal()) {
          throw new TypeHierarchyException("Class " + currentNode.identifier + " is extending a final class " +
            parent.identifier);
        }
      } else if (currentNode.classOrInterface instanceof InterfaceDeclaration &&
        parent.classOrInterface instanceof ClassDeclaration) {
        throw new TypeHierarchyException("An interface cannot extend a class[Interface " + currentNode.identifier +
          ", class:" + parent.identifier + "]");
      }
      if (parent.getFullname().equals(currentNode.getFullname())) {
        throw new TypeHierarchyException(currentNode.getFullname() + " is extending itself");
      }
    }
  }

  /**
   * Perform verification on the implements clause of a class
   * @param implementedParents interfaces implemented by this class
   * @param currentNode name of the class being processed
   * @throws TypeHierarchyException
   */
  private void verifyImplementedInterfaces(List<HierarchyGraphNode> implementedParents,
                                           HierarchyGraphNode currentNode) throws TypeHierarchyException {
    for (HierarchyGraphNode parent : implementedParents) {
      if (parent.classOrInterface instanceof ClassDeclaration) {
        throw new TypeHierarchyException("A Class cannot implement a class [class: " + currentNode.identifier +
          ", implemented class: " + parent.identifier + "]");
      }
      if (parent.getFullname().equals(currentNode.getFullname())) {
        throw new TypeHierarchyException("Class " + currentNode.identifier + " is implementing an interface with the same name");
      }
    }
  }

  /**
   * Ensure no 2 constructors within a class have the same signature
   */
  private void checkForConstructorDuplication(HierarchyGraphNode currentNode) throws TypeHierarchyException {
    for (int i = 0; i < currentNode.constructors.size(); i++) {
      for (int j = 0; j < currentNode.constructors.size(); j++) {
        if (i != j && currentNode.constructors.get(i).signaturesMatch(currentNode.constructors.get(j))) {
          throw new TypeHierarchyException("Class " + currentNode.identifier + " contains a duplicate constructor");
        }
      }
    }
  }

  /**
   * Use depth first search to see if the Hierarchy Graph is cyclic
   * @return one of the nodes in the cycle to improve our error msg
   */
  private void isCyclic() throws TypeHierarchyException {
    HashSet<HierarchyGraphNode> visited = new HashSet<HierarchyGraphNode>();
    HashSet<HierarchyGraphNode> recursionStack = new HashSet<HierarchyGraphNode>();
    HierarchyGraphNode cyclicNode;

    for (Map.Entry<String, HierarchyGraphNode> entry : graph.nodes.entrySet()) {
      HierarchyGraphNode currentNode = entry.getValue();
      if ((cyclicNode = isCyclicHelper(currentNode, visited, recursionStack)) != null) {
        throw new TypeHierarchyException("Graph is not acyclic.  " +
          cyclicNode.identifier + " is causing cycles in hierarchy checking");
      }
    }
  }

  private HierarchyGraphNode isCyclicHelper(HierarchyGraphNode currentNode, HashSet<HierarchyGraphNode> visited, HashSet<HierarchyGraphNode> recursionStack) {
    if (!visited.contains(currentNode)) {
      visited.add(currentNode);
      recursionStack.add(currentNode);
      for (HierarchyGraphNode child : currentNode.children) {
        if ( (!visited.contains(child) && isCyclicHelper(child, visited, recursionStack) != null) ||
          recursionStack.contains(child)) {
          return child;
        }
      }
    }
    recursionStack.remove(currentNode);
    return null;
  }

  private HierarchyGraphNode verifyMethodHierarchy() throws TypeHierarchyException, DeadCodeException {
    //Skip verifying nodes that have already been verified
    HashSet<HierarchyGraphNode> verified = new HashSet<HierarchyGraphNode>();

    for (Map.Entry<String, HierarchyGraphNode> entry : graph.nodes.entrySet()) {
      HierarchyGraphNode currentNode = entry.getValue();
      verifyExtendedAndImplementedMethods(currentNode, verified);
    }
    return null;
  }

  /**
   * Perform method verification on extended and implemented classes and interfaces
   */
  private List<Method> verifyExtendedAndImplementedMethods(HierarchyGraphNode currentNode, HashSet<HierarchyGraphNode> verified) throws TypeHierarchyException, DeadCodeException {
    List<Method> extendedMethods = new ArrayList<Method>();
    List<Method> implementedMethods = new ArrayList<Method>();
    List<Method> allMethods = new ArrayList<Method>();
    // Abstract methods (including interface methods) not implemented by currentNode
    List<Method> unimplementedMethods = new ArrayList<Method>();

    extendObjectClass(currentNode);

    // Use depth first to start from the top of the hierarchy tree
    // and work our way down. Check extended methods first then
    // the implemented methods, as extends takes precedence
    for (HierarchyGraphNode node : currentNode.extendsList) {
      extendedMethods.addAll(verifyExtendedAndImplementedMethods(node, verified));
    }
    for (HierarchyGraphNode node : currentNode.implementsList) {
      implementedMethods.addAll(verifyExtendedAndImplementedMethods(node, verified));
    }

    abstractMethodChecks(extendedMethods, currentNode, unimplementedMethods);

    allMethods.addAll(extendedMethods);
    allMethods.addAll(currentNode.methods);
    implementedMethodsCheck(implementedMethods, allMethods, currentNode, unimplementedMethods);

    if (!verified.contains(currentNode)) {
      verifyOwnedMethods(currentNode);
      verified.add(currentNode);
    };
    extendedMethodChecks(currentNode, extendedMethods, unimplementedMethods);
    if (currentNode.classOrInterface instanceof ClassDeclaration) allMethods.addAll(unimplementedMethods);
    return allMethods;
  }

  /**
   * Performs 2 checks
   *  abstract methods of a nonabstract class are implemented
   *  non-abstract classes do not have abstract methods
   * @param extendedMethods
   * @param currentNode
   * @param unimplementedMethods
   * @throws TypeHierarchyException
   */
  private void abstractMethodChecks(List<Method> extendedMethods, HierarchyGraphNode currentNode, List<Method> unimplementedMethods) throws TypeHierarchyException {
    for (int i = 0; i < extendedMethods.size(); i++) {
      boolean absractMethod = extendedMethods.get(i).isAbstract() || extendedMethods.get(i).parent.classOrInterface instanceof InterfaceDeclaration;
      boolean nonAbstractClass = false;
      boolean found = false;
      if (!absractMethod) continue;

      // Check extended methods
      for (int j = i+1; j < extendedMethods.size(); j++) {
        if (i != j) {
          if (!extendedMethods.get(j).parent.isAbstract() && !(extendedMethods.get(j).parent.classOrInterface instanceof InterfaceDeclaration)) {
            nonAbstractClass = true;
          }
          if (extendedMethods.get(j).signaturesMatch(extendedMethods.get(i))) {
            found = true;
          }
        }
      }

      // Check the class's methods
      for (Method method : currentNode.methods) {
        if (method.signaturesMatch(extendedMethods.get(i))) {
          found = true;
        }
      }
      if (!currentNode.isAbstract() && !(currentNode.classOrInterface instanceof InterfaceDeclaration)) {
        nonAbstractClass = true;
      }

      if (!found && nonAbstractClass) {
        throw new TypeHierarchyException(currentNode.identifier + " contains an abstract method " + extendedMethods.get(i));
      } else if (!found) {
        unimplementedMethods.add(extendedMethods.get(i));
      }
    }
  }

  /**
   * Performs checks on implemented methods
   */
  public void implementedMethodsCheck(List<Method> implementedMethods,
                                      List<Method> methodsAvailable,
                                      HierarchyGraphNode currentNode,
                                      List<Method> unimplementedMethods) throws TypeHierarchyException, DeadCodeException {
    if (currentNode.classOrInterface instanceof InterfaceBody) {
      throw new DeadCodeException("interface " + currentNode.getFullname() + " has implemented methods");
    }
    boolean found = false;
    for (int i = 0; i < implementedMethods.size(); i++) {
      for (int j = i+1; j < implementedMethods.size(); j++) {
        if (implementedMethods.get(i).signaturesMatch(implementedMethods.get(j)) &&
          !implementedMethods.get(i).returnType.equals(implementedMethods.get(j).returnType) &&
          !implementedMethods.get(i).parent.equals(implementedMethods.get(j).parent)) {
          throw new TypeHierarchyException("Method " + implementedMethods.get(i).identifier + " is implemented " +
            "from interfaces " + implementedMethods.get(i).parent.getFullname() + " and " +
            implementedMethods.get(j).parent.getFullname() +
            " by " + currentNode.getFullname() + " but their return types are different");
        }
      }
      for (Method method : methodsAvailable) {
        if (method.signaturesMatch(implementedMethods.get(i))) {
          if (method.isProtected() && implementedMethods.get(i).isPublic()) {
            throw new TypeHierarchyException(currentNode.identifier + " contains a protected method " +
              "which replaces a public method [" + method.identifier + "]");
          }
          if (!method.returnType.equals(implementedMethods.get(i).returnType)) {
            throw new TypeHierarchyException(currentNode.identifier + " implements " + method.identifier +
              " with a different return type");
          }
          found = true;
          break;
        }
      }
      if (!found  && !currentNode.isAbstract()) {
        throw new TypeHierarchyException("Class " + currentNode.identifier + " implements " + implementedMethods.get(i).classOrInterfaceName +
          " but does not implement function " + implementedMethods.get(i).identifier);
      } else if (!found) {
        unimplementedMethods.add(implementedMethods.get(i));
      }
    }
  }

  private void extendedMethodChecks(HierarchyGraphNode currentNode, List<Method> extendedMethods, List<Method> unimplementedMethods) throws TypeHierarchyException {
    for (Method extendedMethod : extendedMethods) {
      for (Method method : currentNode.methods) {
        methodCheck(method, extendedMethod);
      }
    }
    for (int i = 0; i < extendedMethods.size(); i++) {
      for (int j = i+1; j < extendedMethods.size(); j++) {
        if (i != j) {
          methodCheck(extendedMethods.get(j), extendedMethods.get(i));
        }
      }
    }
  }

  private void methodCheck(Method method, Method extendedMethod) throws TypeHierarchyException {
    if (extendedMethod.signaturesMatch(method)) {
      if (!extendedMethod.returnType.equals(method.returnType)) {
        throw new TypeHierarchyException("A class or interface must not contain two methods with the same signature but different return types");
      }
      if (extendedMethod.isStatic() && !method.isStatic()) {
        throw new TypeHierarchyException("A nonstatic method must not replace a static method");
      }
      if (!extendedMethod.isStatic() && method.isStatic()) {
        throw new TypeHierarchyException("A nonstatic method must not replace a static method");
      }
      if (extendedMethod.isPublic() && method.isProtected()) {
        throw new TypeHierarchyException("A protected method must not replace a public method.");
      }
      if (extendedMethod.isFinal()) {
        throw new TypeHierarchyException("A method must not replace a final method.");
      }
    }
  }

  /**
   * Perform checks on the methods owned by a class/interface
   * @param currentNode
   * @throws TypeHierarchyException
   */
  private void verifyOwnedMethods(HierarchyGraphNode currentNode) throws TypeHierarchyException {
    boolean classIsAbstract = currentNode.modifiers.contains(TokenType.ABSTRACT);
    for (int i = 0; i < currentNode.methods.size(); i++) {
      for (int j = 0; j < currentNode.methods.size(); j++) {
        if (i != j && currentNode.methods.get(i).signaturesMatch(currentNode.methods.get(j))) {
          throw new TypeHierarchyException("A method with the exact same signature is found in "
            + currentNode.identifier + " Method: " + currentNode.methods.get(i));
        }
      }
      if (!classIsAbstract &&
        currentNode.classOrInterface instanceof ClassDeclaration &&
        currentNode.methods.get(i).isAbstract()) {
        throw new TypeHierarchyException("Method " + currentNode.methods.get(i).identifier + " is abstract, " +
          "class " + currentNode.identifier + " containing the method is not");
      }
    }
  }

  /**
   * A class not extending any other class automatically extends Object
   */
  private void extendObjectClass(HierarchyGraphNode currentNode) {
    if (currentNode.extendsList.size() == 0 &&
      currentNode.classOrInterface instanceof ClassDeclaration &&
      !currentNode.getFullname().equals(OBJECT_CLASS)) {
      currentNode.extendsList.add(graph.nodes.get(OBJECT_CLASS));
      graph.nodes.get(OBJECT_CLASS).children.add(currentNode);
    }
  }
}
