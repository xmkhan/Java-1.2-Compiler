package type.hierarchy;

import exception.DeadCodeException;
import exception.TypeHierarchyException;
import token.*;

import java.util.*;

/**
 * Creates an acyclic class hierarchy graph for all types known to the program.
 */
public class CompilationUnitsToHierarchyGraphConverter {
  private HierarchyGraph graph;
  public Map<CompilationUnit, HierarchyGraphNode> compilationUnitToNode = new HashMap<CompilationUnit, HierarchyGraphNode>();

  public CompilationUnitsToHierarchyGraphConverter() {}

  /**
   * Creates a hierarchy graph of all the interfaces and classes being compiled
   * @param compilationUnits list of all the CompilationUnits (one per input file)
   * @throws TypeHierarchyException
   */
  public HierarchyGraph convert(List<CompilationUnit> compilationUnits) throws DeadCodeException, TypeHierarchyException {
    graph = new HierarchyGraph();
    compilationUnitToNode = new HashMap<CompilationUnit, HierarchyGraphNode>();
    // Construct the hierarchy graph with only the nodes representing the files
    for (CompilationUnit compilationUnit : compilationUnits) {
      compilationUnitToNode.put(compilationUnit, createNode(compilationUnit));
    }

    // Fill in each node and add node relationships
    for (CompilationUnit compilationUnit : compilationUnits) {
      processNode(compilationUnit, compilationUnitToNode.get(compilationUnit));
    }
    return graph;
  }

  /**
   * Add a node to the graph
   * @throws TypeHierarchyException
   */
  public HierarchyGraphNode createNode(CompilationUnit compilationUnit) throws DeadCodeException, TypeHierarchyException {
    Token classOrInterface = compilationUnit.children.get(compilationUnit.children.size()-1).children.get(0);
    String packageName = extractPackageName(compilationUnit);
    String fullNodeName = constructFullName(packageName, classOrInterface);
    HierarchyGraphNode node = graph.createNode(fullNodeName);
    node.packageName = packageName;
    node.setImportDeclarations(extractImports(compilationUnit));
    return node;
  }

  /**
   * Update a node with the information we need and
   * add node relationships in the graph
   * @throws TypeHierarchyException
   */
  public void processNode(CompilationUnit compilationUnit, HierarchyGraphNode node) throws TypeHierarchyException, DeadCodeException {
    Token classOrInterface = compilationUnit.children.get(compilationUnit.children.size()-1).children.get(0);
    processTypeDeclarationToken(classOrInterface, node);
  }

  /**
   * Each node in the grpah is represented by it's full name
   * which is packageName.classOrInterfaceName
   * or
   * classOrInterfaceName if there is no package declaration
   */
  private String constructFullName(String packageName, Token classOrInterface) throws DeadCodeException {
    StringBuilder fullName = new StringBuilder();
    if (packageName.length() > 0) {
      fullName.append(packageName).append(".");
    }
    if (classOrInterface instanceof ClassDeclaration) {
      return fullName.append(((ClassDeclaration) classOrInterface).identifier.getLexeme()).toString();
    } else if (classOrInterface instanceof InterfaceDeclaration) {
      return fullName.append(((InterfaceDeclaration) classOrInterface).identifier.getLexeme()).toString();
    } else {
      throw new DeadCodeException("Expecting a ClassDeclaration or InterfaceDeclaration token but received " + classOrInterface.getTokenType());
    }
  }

  private String extractPackageName(CompilationUnit compilationUnit) {
    if (compilationUnit.children.get(0) instanceof PackageDeclaration) {
      return compilationUnit.children.get(0).getLexeme();
    }
    return "";
  }

  private ImportDeclarations extractImports(CompilationUnit compilationUnit) {
    if (compilationUnit.children.get(0) instanceof ImportDeclarations) {
      return ((ImportDeclarations)compilationUnit.children.get(0));
    }
    return null;
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
          node.addModifiers(((Modifiers) token).getModifiers());
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
          List<MethodHeader> methods = new ArrayList<MethodHeader>();
          List<ConstructorDeclaration> constructors = new ArrayList<ConstructorDeclaration>();
          List<BaseMethodDeclaration> allMethodsDeclarations = new ArrayList<BaseMethodDeclaration>();
          List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>();
          extractMethodHeaders((ClassBody) token, fields, methods, constructors, allMethodsDeclarations);
          addMethodsToNode(methods, node);
          addConstructorsToNode(constructors, node);
          node.setBaseMethodDeclarations(allMethodsDeclarations);
          node.setFields(fields);
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
   * Add constructors to a node
   */
  private void addConstructorsToNode(List<ConstructorDeclaration> constructors, HierarchyGraphNode node) throws DeadCodeException {
    if (constructors == null) return;
    for (ConstructorDeclaration constructor : constructors) {
      Method method = new Method();
      method.parent = node;
      node.constructors.add(method);
      method.classOrInterfaceName = node.getFullname();
      for (Token token : constructor.children) {
        switch (token.getTokenType()) {
          case ConstructorDeclarator:
            method.identifier = ((ConstructorDeclarator)token).getIdentifier().getLexeme();
            method.parameterTypes.addAll(extractParameterTypes(((ConstructorDeclarator) token).getParameterList()));
            break;
          case Modifiers:
            method.addModifiers(((Modifiers)token).getModifiers());
            break;
          case ConstructorBody:
            break;
          default:
            throw new DeadCodeException("bad class or interface declaration. TokenType received: " + token.getTokenType());
        }
      }
    }
  }

  /**
   * Add method information, such as parameter types and modifiers to a node
   */
  private void addMethodsToNode(List<MethodHeader> methodHeaders, HierarchyGraphNode node) throws DeadCodeException {
    if (methodHeaders == null) return;
    for (MethodHeader methodHeader : methodHeaders) {
      Method method = new Method();
      method.parent = node;
      node.methods.add(method);
      method.classOrInterfaceName = node.getFullname();
      for (Token token : methodHeader.children) {
        switch (token.getTokenType()) {
          case Type:
            method.returnType = ((Type) token).getType().getLexeme();
            break;
          case MethodDeclarator:
            method.identifier = ((MethodDeclarator)token).identifier;
            method.parameterTypes.addAll(extractParameterTypes(((MethodDeclarator) token).getParameterList()));
            break;
          case Modifiers:
            method.addModifiers(((Modifiers)token).getModifiers());
            break;
          case VOID:
            method.returnType = TokenType.VOID.toString();
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
  private ArrayList<Parameter> extractParameterTypes(FormalParameterList parameterList) {
    ArrayList<Parameter> parameterTypes = new ArrayList<Parameter>();

    if (parameterList == null) return parameterTypes;

    for (FormalParameter formalParameter : parameterList.getFormalParameters()) {
      parameterTypes.add(new Parameter(formalParameter.getType().getLexeme(), formalParameter.isArray()));
    }

    return parameterTypes;
  }

  /**
   * Retrieves all the MethodHeaders from a ClassBody
   */
  private void extractMethodHeaders(ClassBody classBody,
                                    List<FieldDeclaration> fields,
                                    List<MethodHeader> methods,
                                    List<ConstructorDeclaration> constructors,
                                    List<BaseMethodDeclaration> allMethodDeclarations) {
    if (classBody == null || classBody.bodyDeclarations == null) return;
    for (ClassBodyDeclaration classBodyDeclaration : classBody.bodyDeclarations.getBodyDeclarations()) {
      if (classBodyDeclaration.isMethod()) {
        allMethodDeclarations.add((MethodDeclaration) (classBodyDeclaration.children.get(0).children.get(0)));
        methods.add(((MethodDeclaration) (classBodyDeclaration.children.get(0).children.get(0))).methodHeader);
      }
      if (classBodyDeclaration.isField()) {
        fields.add((FieldDeclaration) (classBodyDeclaration.children.get(0).children.get(0)));
      }
      if (classBodyDeclaration.isConstructor()) {
        allMethodDeclarations.add((ConstructorDeclaration) classBodyDeclaration.declaration);
        constructors.add((ConstructorDeclaration) classBodyDeclaration.declaration);
      }
    }
  }

  /**
   * Retrieves all MethodHeaders from an InterfaceBody
   * @param interfaceBody
   * @return
   */
  private List<MethodHeader> extractMethodHeaders(InterfaceBody interfaceBody) {
    List<MethodHeader> methodHeaders = new ArrayList<MethodHeader>();
    if (interfaceBody == null || interfaceBody.getInterfaceMemberDeclaration() == null) return methodHeaders;
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
    HierarchyGraphNode parentNode = graph.getNode(child, name, child.getImportList());
    if (child.hasParent(parentNode.getFullname())) {
      throw new TypeHierarchyException("Interface " + name + " is repeated in the TypeDeclaration of " + child.identifier);
    }

    parentNode.children.add(child);

    if (tokenType.equals(TokenType.Super) ||
      tokenType.equals(TokenType.EXTENDS) ||
      tokenType.equals(TokenType.ExtendsInterfaces)) {
      child.extendsList.add(parentNode);
    } else {
      child.implementsList.add(parentNode);
    }
  }
}
