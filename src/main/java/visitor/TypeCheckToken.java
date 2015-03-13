package visitor;

import token.*;

public class TypeCheckToken {

  public TokenType tokenType;
  public boolean isArray;
  public Declaration declaration;

  public TypeCheckToken(TokenType type) {
    this.tokenType = type;
  }

  public TypeCheckToken(TokenType type, boolean isArray) {
    this.tokenType = type;
    this.isArray = isArray;
  }

  public TypeCheckToken (Declaration token) {
    declaration = token;

    if(token instanceof FieldDeclaration) {
      isArray = ((FieldDeclaration) token).type.isArray();
      tokenType = ((FieldDeclaration) token).type.getType().getTokenType();
    } else if (token instanceof MethodDeclaration) {
      isArray = ((MethodDeclaration) token).methodHeader.type.isArray();
      tokenType = ((MethodDeclaration) token).methodHeader.type.getType().getTokenType();
    } else if (token instanceof LocalVariableDeclaration) {
      isArray = ((LocalVariableDeclaration) token).type.isArray();
      tokenType = ((LocalVariableDeclaration) token).type.getTokenType();
    } else if(token instanceof FormalParameter) {
      isArray = ((FormalParameter) token).type.isArray();
      tokenType = ((FormalParameter) token).type.getType().getTokenType();
    } else if(token instanceof ClassDeclaration) {
      isArray = false;
      tokenType = TokenType.OBJECT;
    }

    this.declaration = token;
  }

  @Override
  public String toString() {
    return tokenType.toString() + " isArray = " + isArray;
  }

}
