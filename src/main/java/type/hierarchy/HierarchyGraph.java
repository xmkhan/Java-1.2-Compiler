package type.hierarchy;

import exception.DeadCodeException;
import exception.TypeHierarchyException;
import token.*;

import java.util.HashMap;

/**
 * Creates an acyclic class hierarchy graph for all types known to the program.
 */
public class HierarchyGraph {
  public HashMap<String, HierarchyGraphNode> nodes;

  public HierarchyGraph() {
    this.nodes = new HashMap<>();
  }

  /**
   * Add a node to the graph
   * @throws TypeHierarchyException
   */
  public HierarchyGraph addNode(Token classOrInterface) throws TypeHierarchyException, DeadCodeException {
    HierarchyGraphNode node = null;
    for (Token token : classOrInterface.children) {
      if (token.getTokenType().equals(TokenType.IDENTIFIER)) {
        node = createNodeIfItDoesntExist(token.getLexeme());
      }
    }

    if (node == null) {
      throw new DeadCodeException("Failed to fetch or create a HierarchyGraphNode");
    }

    node.classOrInterface = classOrInterface;

    // Process the TypeDeclaration token
    for (Token token : classOrInterface.children) {
      switch (token.getTokenType()) {
        case Modifiers:
          extractModifiers((token.Modifiers) token, node);
          break;
        case IDENTIFIER:
          node.identifier = token.getLexeme();
          break;
        case Super:
          extend((Super) token, node);
          break;
        case Interfaces:
          implementsInterfaces(token, node);
          break;
        case ExtendsInterfaces:
          extendsInterfaces(token, node);
          break;
        case CLASS:
          break;
        case INTERFACE:
          break;
        case ClassBody:
          break;
        case InterfaceBody:
          break;
        default:
          throw new DeadCodeException("bad class or interface declaration. TokenType received: " + token.getTokenType());
      }
    }
    return this;
  }

  /**
   * Creates a Set from modifiers (public, abstract, final, ...) used in the TypeDeclaration
   * token, and adds it to the corresponding node in the graph
   * @param modifiers Modifiers token which includes one or more modifiers
   * @param node Add the modifiers to this node
   */
  private void extractModifiers(Modifiers modifiers, HierarchyGraphNode node) {
    Token temp = modifiers;

    while(temp.children.get(0).getTokenType().equals(TokenType.Modifiers)) {
      node.modifiers.add(temp.children.get(1).children.get(0).getTokenType());
      temp = temp.children.get(0);
    }
    node.modifiers.add(temp.children.get(0).children.get(0).getTokenType());
  }

  /**
   * Handles a class extending another.  Add the extended class to the Hierarchy graph
   * if it does not exist
   * @param extend class getting extended
   * @param node node representing the class extending
   * @throws TypeHierarchyException
   */
  private void extend(Super extend, HierarchyGraphNode node) throws TypeHierarchyException {
    Token token = extend.children.get(1).children.get(0).children.get(0).children.get(0).children.get(0);

    if(!token.getTokenType().equals(TokenType.IDENTIFIER)) {
      throw new TypeHierarchyException("Expected a QualifiedName, but received a QualifiedName");
    }

    updateNodeRelationships(token.getLexeme(), node, extend.getTokenType());
  }

  /**
   * Handles a class implementing one or more interfaces.  Adds the implemented
   * interfaces to the Hierarchy graph if they do not exist.
   * @param interfaces Interfaces token
   * @param node The node representing the interface extending
   * @throws TypeHierarchyException
   */
  private void implementsInterfaces(Token interfaces, HierarchyGraphNode node) throws TypeHierarchyException {
    String interfaceName;
    Token interfaceList = interfaces.children.get(1);

    while(interfaceList.children.get(0).getTokenType().equals(TokenType.InterfaceTypeList)) {
      interfaceName = getClassOrInterfaceIdentifier(interfaceList.children.get(2)).getLexeme();
      updateNodeRelationships(interfaceName, node, interfaces.getTokenType());
      interfaceList = interfaceList.children.get(0);
    }

    interfaceName = getClassOrInterfaceIdentifier(interfaceList).getLexeme();
    updateNodeRelationships(interfaceName, node, interfaces.getTokenType());
  }

  /**
   * Handles an Interface extending one or more interfaces.  Adds the extended extended
   * interfaces to the Hierarchy graph if they do not exist.
   * @param interfaces Interfaces token
   * @param node The node representing the interface extending
   * @throws TypeHierarchyException
   */
  private void extendsInterfaces(Token interfaces, HierarchyGraphNode node) throws TypeHierarchyException {
    Token interfaceList = interfaces;
    while(true) {
      String interfaceName = null;
      if (interfaceList.children.get(0).getTokenType().equals(TokenType.ExtendsInterfaces)) {
        interfaceName = getClassOrInterfaceIdentifier(interfaceList.children.get(2)).getLexeme();
        updateNodeRelationships(interfaceName, node, interfaces.getTokenType());
        interfaceList = interfaceList.children.get(0);
      } else if (interfaceList.children.get(0).getTokenType().equals(TokenType.EXTENDS)) {
        interfaceName = getClassOrInterfaceIdentifier(interfaceList.children.get(1)).getLexeme();
        updateNodeRelationships(interfaceName, node, interfaces.getTokenType());
        break;
      }
    }
  }

  /**
   * Set the child/parent relationships. Create a node for the parent if it does not exist
   * @throws TypeHierarchyException
   */
  private void updateNodeRelationships(String name, HierarchyGraphNode child, TokenType tokenType) throws TypeHierarchyException {
    if (child.hasParent(name)) {
      throw new TypeHierarchyException("Interface " + name + " is repeated in the TypeDeclaration of " + child.identifier);
    }

    HierarchyGraphNode parentNode = createNodeIfItDoesntExist(name);
    parentNode.children.add(child);

    if (tokenType.equals(TokenType.SUPER) || tokenType.equals(TokenType.ExtendsInterfaces)) {
      child.extendsList.add(parentNode);
    } else {
      child.implementsList.add(parentNode);
    }
  }

  /**
   * Returns the node corresponding to the passed in name.
   * Creates a new node if it doesn't exist
   */
  private HierarchyGraphNode createNodeIfItDoesntExist(String name) {
    HierarchyGraphNode node;
    if (nodes.containsKey(name)) {
      node = nodes.get(name);
    } else {
      node = new HierarchyGraphNode();
      node.identifier = name;
      nodes.put(name, node);
    }
    return node;
  }

  /**
   * Takes in an InterfaceType or ClassTypes token and returns the associated Identifier token
   */
  private Token getClassOrInterfaceIdentifier(Token token) {
    while(token.children != null && !token.getTokenType().equals(TokenType.IDENTIFIER)) {
      token = token.children.get(0);
    }
    return token;
  }
}
