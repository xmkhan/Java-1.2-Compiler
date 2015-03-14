package visitor;

import token.*;

public class TypeCheckToken {

  public TokenType tokenType;
  public boolean isArray;
  public Declaration declaration;
  public String absolutePath;

  public TypeCheckToken(TokenType type) {
    this.tokenType = type;
  }

  public TypeCheckToken(TokenType type, boolean isArray) {
    this.tokenType = type;
    this.isArray = isArray;
  }

  public TypeCheckToken (Declaration token) {
    this.declaration = token;

    if(token instanceof ClassDeclaration) {
      isArray = false;
      tokenType = TokenType.OBJECT;
    } else {
      isArray = token.type.isArray();
      if(token.type.primitiveType != null) {
        tokenType = token.type.getType().getTokenType();
      } else if(token.type.referenceType != null) {
        if (token.type.referenceType.classOrInterfaceType != null || token.type.referenceType.arrayType.name != null) {
          tokenType = TokenType.OBJECT;
        } else if(token.type.referenceType.arrayType.primitiveType != null) {
          tokenType = token.type.getType().getTokenType();
        }
      }
    }
  }

  public String getAbsolutePath() {
    if(absolutePath == null || absolutePath.isEmpty()) {
      return declaration.getAbsolutePath();
    } else {
      return absolutePath;
    }
  }

  public boolean isPrimitiveType() {
    return tokenType == TokenType.BOOLEAN_LITERAL ||
            tokenType == TokenType.INT_LITERAL ||
            tokenType == TokenType.CHAR_LITERAL ||
            tokenType == TokenType.BYTE ||
            tokenType == TokenType.SHORT;
  }

  @Override
  public String toString() {
    return tokenType.toString() + " isArray = " + isArray;
  }

}
