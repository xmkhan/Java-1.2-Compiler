package type.hierarchy;

import token.Modifier;
import token.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a class/interface method
 */
public class Method {
  // Modifiers for the class or interface
  public List<Modifier> modifiers;

  public String identifier;

  public List<Parameter> parameterTypes;

  public Method() {
    modifiers = new ArrayList<Modifier>();
  }

  public boolean isFinal() {
    return modifiers.contains(TokenType.FINAL);
  }

  public boolean isStatic() {
    return modifiers.contains(TokenType.STATIC);
  }

  public boolean isAbstract() {
    return modifiers.contains(TokenType.ABSTRACT);
  }

  public boolean signaturesMatch(Method method) {
    return method.identifier == this.identifier && parameterTypesMatch(method);
  }

  private boolean parameterTypesMatch(Method method) {
    if (method.parameterTypes.size() != this.parameterTypes.size()) {
      return false;
    }
    for (int i = 0; i < parameterTypes.size(); i++) {
      if (parameterTypes.get(i) != method.parameterTypes.get(i)) {
        return false;
      }
    }
    return true;
  }
}
