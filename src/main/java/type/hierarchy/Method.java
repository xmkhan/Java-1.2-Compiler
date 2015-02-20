package type.hierarchy;

import com.sun.org.apache.xpath.internal.operations.Mod;
import token.Modifier;
import token.Token;
import token.TokenType;

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
      if (parameterTypes.get(i) != method.parameterTypes.get(i)) {
        return false;
      }
    }
    return true;
  }
}
