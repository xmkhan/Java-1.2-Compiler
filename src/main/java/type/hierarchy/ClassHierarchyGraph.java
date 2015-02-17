package type.hierarchy;

import exception.TypeHierarchyException;
import token.*;

import java.util.HashMap;

/**
 * Creates a class hierarchy acyclic graph for all types known to the program.
 */
public class ClassHierarchyGraph {
  public HashMap<String, ClassNode> graph;

  public ClassHierarchyGraph() {
    this.graph = new HashMap<String, ClassNode>();
  }

  public ClassHierarchyGraph addClass(ClassDeclaration clazz) throws TypeHierarchyException {
    ClassNode node = getClassNode(clazz);
    if(node == null) {
      // dead code
      return this;
    }
    constructClassNode(node, clazz);
    return this;
  }

  public ClassHierarchyGraph addInterface(InterfaceDeclaration baseInterface) {
    return this;
  }

  private void extend(Super clazz, ClassNode node) throws TypeHierarchyException {
    Token token = clazz.children.get(1).children.get(0).children.get(0).children.get(0).children.get(0);

    if(!token.getTokenType().equals(TokenType.IDENTIFIER)) {
      throw new TypeHierarchyException("Expected a QualifiedName, but received a QualifiedName");
    }

    updateNodeRelationships(token.getLexeme(), node);
  }

  private void interfaces(Token interfaces, ClassNode node) throws TypeHierarchyException {
    Token interfaceList = interfaces.children.get(1);
    updateNodeRelationships(interfaceList.getLexeme(), node);

    while(interfaceList.children.get(0).getTokenType().equals(TokenType.InterfaceTypeList) ||
      interfaceList.children.get(0).getTokenType().equals(TokenType.ExtendsInterfaces)) {
      String interfaceName =
        interfaceList.children.get(2).children.get(0).children.get(0).children.get(0).children.get(0).getLexeme();
      System.out.println(interfaceName);
      updateNodeRelationships(interfaceName, node);
      interfaceList = interfaceList.children.get(0);
    }

    String interfaceName =
      interfaceList.children.get(0).children.get(0).children.get(0).children.get(0).children.get(0).getLexeme();
    System.out.println(interfaceName);
    updateNodeRelationships(interfaceName, node);
  }

  private void extractModifiers(ClassNode node, Modifiers modifiers) {
    Modifiers temp = modifiers;

    while(temp.children.get(0).getTokenType().equals(TokenType.Modifiers)) {
      node.modifiers.add((Modifier) temp.children.get(1));
    }
  }

  private void updateNodeRelationships(String name, ClassNode child) {
    ClassNode node;
    if (!graph.containsKey(name)) {
      node = new ClassNode();
      node.identifier = name;

    } else {
      node = graph.get(name);
    }
    node.children.add(child);
    child.extendsList.add(node);
    graph.put(name, node);
  }

  private void constructClassNode(ClassNode node, ClassDeclaration clazz) throws TypeHierarchyException {
    node.classOrInterface = clazz;
    for (Token token : clazz.children) {
      System.out.println("Token type: " + token.getTokenType());
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
        case CLASS:
          break;
        case ClassBody:
          break;
        case InterfaceBody:
          break;
        case ExtendsInterfaces:
          interfaces(token, node);
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
        if (graph.containsKey(token.getLexeme())) {
          node = graph.get(classOrInterface.getLexeme());
        } else {
          node = new ClassNode();
          graph.put(token.getLexeme(), node);
        }
      }
    }
    return node;
  }
}
