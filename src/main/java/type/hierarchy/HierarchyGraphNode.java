package type.hierarchy;

import token.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper that represents a Class/Interface with additional attributes used by ObjectHierarchyGraph.
 */
public class HierarchyGraphNode {
  // Internal pointer to the ASTNode.
  public Token classOrInterface;
  // List of all children.
  public List<HierarchyGraphNode> children;
  // List of classes/interfaces in the extends clause.  They point to the corresponding ClassNodes in HierarchyGraph
  public List<HierarchyGraphNode> extendsList;
  // List of interfaces in the implements clause.  They point to the corresponding ClassNodes in HierarchyGraph
  public List<HierarchyGraphNode> implementsList;
  // Modifiers for the class or interface
  public List<TokenType> modifiers;
  // All the information regarding methods of a class/interface
  public List<Method> methods;
  //pointer to BaseMethodDeclaration AST nodes
  public List<BaseMethodDeclaration> baseMethodDeclarations = new ArrayList<BaseMethodDeclaration>();
  // Class/interface identifier
  public String identifier;
  // List of constructors
  public List<Method> constructors;
  // List of class fields
  public List<FieldDeclaration> fields = new ArrayList<FieldDeclaration>();
  public String packageName;
  private ImportDeclarations importDeclarations;

  public HierarchyGraphNode() {
    children = new ArrayList<HierarchyGraphNode>();
    extendsList = new ArrayList<HierarchyGraphNode>();
    implementsList = new ArrayList<HierarchyGraphNode>();
    modifiers = new ArrayList<TokenType>();
    methods = new ArrayList<Method>();
    constructors = new ArrayList<Method>();
  }

  public boolean isFinal() {
    return modifiers.contains(TokenType.FINAL);
  }

  public boolean isAbstract() {
    return modifiers.contains(TokenType.ABSTRACT);
  }

  /**
   * Checks to see if a parent with the specified name already exists.
   */
  public boolean hasParent(String name) {
    return hasParent(name, extendsList) || hasParent(name, implementsList);
  }

  public void setFields(List<FieldDeclaration> fields) {
    this.fields = fields;
  }

  public List<ImportDeclaration> getImportList() {
    return importDeclarations == null ? new ArrayList<ImportDeclaration>() : importDeclarations.importDeclarations;
  }

  public void setImportDeclarations(ImportDeclarations importDeclarations) {
    this.importDeclarations = importDeclarations;
  }

  public String getPackageName() {
    return packageName != null && packageName.length() > 0 ? packageName + "." : "";
  }

  public String getFullname() {
    return getPackageName() + identifier;
  }

  public void addModifiers(List<Modifier> newModifiers) {
    for (Modifier modifier : newModifiers) {
      modifiers.add(modifier.getModifier().getTokenType());
    }
  }

  private boolean hasParent(String name, List<HierarchyGraphNode> parents) {
    for (HierarchyGraphNode node : parents) {
      if (node.identifier.equals(name)) {
        return true;
      }
      if (HierarchyUtil.checkWithImports(getImportList(), name, node.identifier) ||
        HierarchyUtil.checkWithImports(node.getImportList(), node.identifier, name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Traverse the graph and fetch all classes connected to this node.
   */
  public List<Token> getAllBaseClasses() {
    List<Token> allBaseClasses = new ArrayList<Token>();
    for (HierarchyGraphNode node : extendsList) {
      allBaseClasses.addAll(node.getAllBaseClasses());
    }
    for (HierarchyGraphNode node : implementsList) {
      allBaseClasses.addAll(node.getAllBaseClasses());
    }
    allBaseClasses.add(classOrInterface);
    return allBaseClasses;
  }

  /**
   * Traverse the graph and fetch all interfaces.
   */
  public List<Token> getAllInterfaces() {
    List<Token> allInterfaces = new ArrayList<Token>();
    for (HierarchyGraphNode interfaceNode : implementsList) {
      allInterfaces.add(interfaceNode.classOrInterface);
    }
    for (HierarchyGraphNode node : extendsList) {
      allInterfaces.addAll(node.getAllInterfaces());
    }
    return allInterfaces;
  }

  /**
   * Traverse the graph and fetch all the fields extended by this class
   * @return this node's fields + all the extended fields
   */

  public List<FieldDeclaration> getAllFields() {
    List<FieldDeclaration> allFields = new ArrayList<FieldDeclaration>();
    allFields.addAll(fields);
    for (HierarchyGraphNode node : extendsList) {
      allFields.addAll(node.getAllFields());
    }
    return allFields;
  }

  public List<FieldDeclaration> getAllFieldsReverse() {
    List<FieldDeclaration> allFields = new ArrayList<FieldDeclaration>();
    for (HierarchyGraphNode node : extendsList) {
      allFields.addAll(node.getAllFieldsReverse());
    }
    allFields.addAll(fields);
    return allFields;
  }

  public List<FieldDeclaration> getAllBaseFields() {
    List<FieldDeclaration> baseFields = new ArrayList<FieldDeclaration>();
    for (HierarchyGraphNode node : extendsList) {
      baseFields.addAll(node.getAllFields());
    }
    return baseFields;
  }

  /**
   * Traverse the graph and return this class/interface's methods, the methods it extends,
   * and the methods it is supposed to implement
   */

  public List<BaseMethodDeclaration> getAllMethods() {
    List<BaseMethodDeclaration> allMethods = new ArrayList<BaseMethodDeclaration>();

    allMethods.addAll(baseMethodDeclarations);

    for (HierarchyGraphNode node : extendsList) {
      allMethods.addAll(node.getAllMethods());
    }
    for (HierarchyGraphNode node : implementsList) {
      allMethods.addAll(node.getAllMethods());
    }
    return allMethods;
  }

  public boolean isDefaultConstructorVisibleToChildren() {
    for (Method constructor : constructors) {
      if (constructor.parameterTypes.size() == 0) return true;
    }
    return false;
  }

  public List<BaseMethodDeclaration> getAllBaseMethods() {
    List<BaseMethodDeclaration> baseMethods = new ArrayList<BaseMethodDeclaration>();

    for (HierarchyGraphNode node : extendsList) {
      baseMethods.addAll(node.getAllMethods());
    }
    for (HierarchyGraphNode node : implementsList) {
      baseMethods.addAll(node.getAllMethods());
    }
    return baseMethods;
  }

  public void setBaseMethodDeclarations(List<BaseMethodDeclaration> baseMethodDeclarations) {
    this.baseMethodDeclarations = baseMethodDeclarations;
  }


}
