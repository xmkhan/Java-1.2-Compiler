package type.hierarchy;

import exception.DeadCodeException;
import exception.TypeHierarchyException;
import token.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Creates an acyclic class hierarchy graph for all types known to the program.
 */
public class HierarchyGraph {
  public HashMap<String, HierarchyGraphNode> nodes;

  public HierarchyGraph() {
    this.nodes = new HashMap<String, HierarchyGraphNode>();
  }

  /**
   * Add a node to the graph
   * @throws TypeHierarchyException
   */
  public HierarchyGraph addNode(Token classOrInterface) throws TypeHierarchyException, DeadCodeException {
    HierarchyGraphNode node = null;
    if (classOrInterface instanceof ClassDeclaration) {
      node = createNodeIfItDoesntExist(((ClassDeclaration) classOrInterface).identifier.getLexeme());
    } else {
      node = createNodeIfItDoesntExist(((InterfaceDeclaration) classOrInterface).identifier.getLexeme());
    }

    processTypeDeclarationToken(classOrInterface, node);
    return this;
  }

  /**
   * Add the data we need for Hierarchy checking to the node associated to the class or interface
   * we are processing
   */
  private void processTypeDeclarationToken(Token classOrInterface, HierarchyGraphNode node) throws DeadCodeException, TypeHierarchyException {
    if (node == null) {
      throw new DeadCodeException("Failed to fetch or create a HierarchyGraphNode");
    }

    node.classOrInterface = classOrInterface;
    for (Token token : classOrInterface.children) {
      switch (token.getTokenType()) {
        case Modifiers:
          node.modifiers = ((Modifiers) token).getModifiers();
          break;
        case IDENTIFIER:
          node.identifier = token.getLexeme();
          break;
        case Super:
          extend((Super) token, node);
          break;
        case Interfaces:
          implementsInterfaces((Interfaces) token, node);
          break;
        case ExtendsInterfaces:
          extendsInterfaces((ExtendsInterfaces) token, node);
          break;
        case ClassBody:
          addMethodsToNode(extractMethodHeaders((ClassBody) token), node);
          break;
        case InterfaceBody:
          addMethodsToNode(extractMethodHeaders((InterfaceBody) token), node);
          break;
        case CLASS:
          break;
        case INTERFACE:
          break;
        default:
          throw new DeadCodeException("bad class or interface declaration. TokenType received: " + token.getTokenType());
      }
    }
  }

  /**
   * Add method information, such as parameter types and modifiers to the node
   */
  private void addMethodsToNode(List<MethodHeader> methodHeaders, HierarchyGraphNode node) throws DeadCodeException {
    for (MethodHeader methodHeader : methodHeaders) {
      Method method = new Method();
      for (Token token : methodHeader.children) {
        switch (token.getTokenType()) {
          case Type:
            break;
          case MethodDeclarator:
            method.identifier = ((MethodDeclarator)token).identifier;
            method.parameterTypes = extractParameterTypes((MethodDeclarator) token);
            break;
          case Modifiers:
            method.modifiers = ((Modifiers)token).getModifiers();
            break;
          case VOID:
            break;
          default:
            throw new DeadCodeException("bad class or interface declaration. TokenType received: " + token.getTokenType());
        }
      }
    }
  }

  /**
   * Extract the parameters of the MethodDeclarator passed in
   */
  private ArrayList<Parameter> extractParameterTypes(MethodDeclarator methodDeclarator) {
    ArrayList<Parameter> parameterTypes = new ArrayList<Parameter>();
    FormalParameterList parameterList = methodDeclarator.getParameterList();

    if (parameterList == null) return null;

    for (FormalParameter formalParameter : parameterList.getFormalParameters()) {
      parameterTypes.add(new Parameter(formalParameter.getType().getLexeme(), formalParameter.isArray()));
    }

    return parameterTypes;
  }

  /**
   * Retrieves all the MethodHeaders in the ClassBody passed in
   */
  private List<MethodHeader> extractMethodHeaders(ClassBody classBody) {
    if (classBody.bodyDeclarations == null) return new ArrayList<MethodHeader>();
    List<MethodHeader> methodHeaders = new ArrayList<MethodHeader>();
    for (ClassBodyDeclaration classBodyDeclaration : classBody.bodyDeclarations.getBodyDeclarations()) {
      if (classBodyDeclaration.isMethod()) {
        methodHeaders.add(((MethodDeclaration) (classBodyDeclaration.children.get(0).children.get(0))).methodHeader);
      }
    }
    return methodHeaders;
  }

  private List<MethodHeader> extractMethodHeaders(InterfaceBody interfaceBody) {
    if (interfaceBody.getInterfaceMemberDeclaration() == null) return new ArrayList<MethodHeader>();
    List<MethodHeader> methodHeaders = new ArrayList<MethodHeader>();
    for (InterfaceMemberDeclaration interfaceMemberDeclaration : interfaceBody.getInterfaceMemberDeclaration().getMemberDeclarations()) {
      methodHeaders.add(interfaceMemberDeclaration.getMethodHeader());
    }
    return methodHeaders;
  }

  /**
   * Handles a class extending another.  Add the extended class to the Hierarchy graph
   * if it does not exist
   * @param extend class getting extended
   * @param node node representing the class extending
   * @throws TypeHierarchyException
   */
  private void extend(Super extend, HierarchyGraphNode node) throws TypeHierarchyException {
    updateNodeRelationships(extend.getType().getLexeme(), node, extend.getTokenType());
  }

  /**
   * Handles a class implementing one or more interfaces.  Adds the implemented
   * interfaces to the Hierarchy graph if they do not exist.
   * @param interfaces Interfaces token
   * @param node The node representing the interface extending
   * @throws TypeHierarchyException
   */
  private void implementsInterfaces(Interfaces interfaces, HierarchyGraphNode node) throws TypeHierarchyException {
    for (InterfaceType interfaceType : interfaces.interfaceTypeList.types) {
      updateNodeRelationships(interfaceType.getType().getLexeme(), node, interfaces.getTokenType());
    }
  }

  /**
   * Handles an Interface extending one or more interfaces.  Adds the extended extended
   * interfaces to the Hierarchy graph if they do not exist.
   * @param interfaces Interfaces token
   * @param node The node representing the interface extending
   * @throws TypeHierarchyException
   */
  private void extendsInterfaces(ExtendsInterfaces interfaces, HierarchyGraphNode node) throws TypeHierarchyException {
    for (InterfaceType interfaceType : interfaces.getInterfaceType()) {
      updateNodeRelationships(interfaceType.getType().getLexeme(), node, interfaces.getTokenType());
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
   * Returns the node corresponding to name
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
}
