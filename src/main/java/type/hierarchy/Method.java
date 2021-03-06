package type.hierarchy;

import token.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a class/interface method
 */
public class Method {
  // Modifiers for the class or interface
  public List<TokenType> modifiers;

  public String identifier;

  public String classOrInterfaceName;

  public String returnType;

  public List<Parameter> parameterTypes;

  public HierarchyGraphNode parent;

  public Method() {
    modifiers = new ArrayList<TokenType>();
    parameterTypes = new ArrayList<Parameter>();
  }

  public boolean isFinal() {
    return modifiers.contains(TokenType.FINAL);
  }

  public boolean isStatic() {
    return modifiers.contains(TokenType.STATIC);
  }

  public boolean isProtected() {
    return modifiers.contains(TokenType.PROTECTED);
  }

  public boolean isPublic() {
    return modifiers.contains(TokenType.PUBLIC);
  }

  public boolean isAbstract() {
    return modifiers.contains(TokenType.ABSTRACT);
  }

  public boolean signaturesMatch(Method method) {
    return method.identifier.equals(this.identifier) && parameterTypesMatch(method);
  }

  public void addModifiers(List<Modifier> newModifiers) {
    for (Modifier modifier : newModifiers) {
      modifiers.add(modifier.getModifier().getTokenType());
    }
  }

  private boolean parameterTypesMatch(Method method) {
    if (method.parameterTypes.size() != parameterTypes.size()) {
      return false;
    }
    for (int i = 0; i < parameterTypes.size(); i++) {
      boolean match = false;
      if (!parameterTypes.get(i).type.equals(method.parameterTypes.get(i).type) &&
        !HierarchyUtil.checkWithImports(parent.getImportList(), parameterTypes.get(i).type, method.parameterTypes.get(i).type) &&
        !HierarchyUtil.checkWithImports(method.parent.getImportList(), method.parameterTypes.get(i).type, parameterTypes.get(i).type)){
        return false;
      }
    }
    return true;
  }
}
