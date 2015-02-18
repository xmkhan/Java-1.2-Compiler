package type.hierarchy;

import exception.TypeHierarchyException;
import token.*;

import java.util.HashMap;

/**
 * Creates an acyclic class hierarchy graph for all types known to the program.
 */
public class ClassHierarchyGraph {
  public HashMap<String, ClassNode> nodes;

  public ClassHierarchyGraph() {
    this.nodes = new HashMap<>();
  }

  public ClassHierarchyGraph addNode(Token token) throws TypeHierarchyException {
    ClassNode node = getClassNode(token);
    if (node == null) {
      return this;
    }
    constructClassNode(node, token);
    return this;
  }

  private void extend(Super clazz, ClassNode node) throws TypeHierarchyException {
    Token token = clazz.children.get(1).children.get(0).children.get(0).children.get(0).children.get(0);

    if(!token.getTokenType().equals(TokenType.IDENTIFIER)) {
      throw new TypeHierarchyException("Expected a QualifiedName, but received a QualifiedName");
    }

    updateNodeRelationships(token.getLexeme(), node, clazz.getTokenType());
  }

  private void extendInterface(Token interfaces, ClassNode node) throws TypeHierarchyException {
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

  private void interfaces(Token interfaces, ClassNode node) throws TypeHierarchyException {
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
   * Takes in a InterfaceType or ClassTypes and returns the associated Identifier token
   * @param token
   * @return
   */
  private Token getClassOrInterfaceIdentifier(Token token) {
    while(token.children != null && !token.getTokenType().equals(TokenType.IDENTIFIER)) {
      token = token.children.get(0);
    }
    return token;
  }

  private void extractModifiers(ClassNode node, Modifiers modifiers) {
    Token temp = modifiers;

    while(temp.children.get(0).getTokenType().equals(TokenType.Modifiers)) {
      node.modifiers.add(temp.children.get(1).children.get(0).getTokenType());
      temp = temp.children.get(0);
    }
    node.modifiers.add(temp.children.get(0).children.get(0).getTokenType());
  }

  private void updateNodeRelationships(String name, ClassNode child, TokenType classOrInterface) throws TypeHierarchyException {
    if (child.hasParent(name)) {
      throw new TypeHierarchyException("Interface " + name + " is repeated in the TypeDeclaration of " + child.identifier);
    }

    ClassNode node;
    if (!nodes.containsKey(name)) {
      node = new ClassNode();
      node.identifier = name;

    } else {
      node = nodes.get(name);
    }
    node.children.add(child);

    if (classOrInterface.equals(TokenType.SUPER) || classOrInterface.equals(TokenType.ExtendsInterfaces)) {
      child.extendsList.add(node);
    } else {
      child.implementsList.add(node);
    }
    nodes.put(name, node);
  }

  private void constructClassNode(ClassNode node, Token clazz) throws TypeHierarchyException {
    node.classOrInterface = clazz;
    for (Token token : clazz.children) {
      switch (token.getTokenType()) {
        case Modifiers:
          extractModifiers(node, (token.Modifiers) token);
          break;
        case IDENTIFIER:
          node.identifier = token.getLexeme();
          break;
        case Super:
          extend((Super) token, node);
          break;
        case Interfaces:
          interfaces(token, node);
          break;
        case ExtendsInterfaces:
          extendInterface(token, node);
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
          throw new TypeHierarchyException("bad class or interface declaration:" + token.getTokenType());
      }
    }
  }

  private ClassNode getClassNode(Token classOrInterface) {
    ClassNode node = null;
    for (Token token : classOrInterface.children) {
      if (token.getTokenType().equals(TokenType.IDENTIFIER)) {
        if (nodes.containsKey(token.getLexeme())) {
          node = nodes.get(token.getLexeme());
        } else {
          node = new ClassNode();
          nodes.put(token.getLexeme(), node);
        }
      }
    }
    return node;
  }
}
