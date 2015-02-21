package type.hierarchy;

import token.*;

import java.util.ArrayList;
import java.util.HashSet;
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
  // Class/interface identifier
  public String identifier;
  // List of constructors
  public List<Method> constructors;

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

  public List<ImportDeclaration> getImportList() {
    return importDeclarations == null ? new ArrayList<ImportDeclaration>() : importDeclarations.importDeclarations;
  }

  public void setImportDeclarations(ImportDeclarations importDeclarations) {
    this.importDeclarations = importDeclarations;
  }

  private boolean hasParent(String name, List<HierarchyGraphNode> parents) {
    for (HierarchyGraphNode node : parents) {
      if (node.identifier.equals(name)) {
        return true;
      }
    }
    return false;
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
}
