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
      if(token.type.isPrimitiveType()) {
        tokenType = token.type.getType().getTokenType();
      } else if(token.type.isReferenceType()) {
        tokenType = TokenType.OBJECT;
      }
    }
  }

  public TypeCheckToken (Declaration token, boolean isArray) {
    this(token);
    this.isArray = isArray;
  }

    public String getAbsolutePath() {
    if(absolutePath == null || absolutePath.isEmpty()) {
      return declaration.getAbsolutePath();
    } else {
      return absolutePath;
    }
  }

  public boolean isPrimitiveType() {
    return tokenType == TokenType.BOOLEAN ||
            tokenType == TokenType.INT ||
            tokenType == TokenType.CHAR ||
            tokenType == TokenType.BYTE ||
            tokenType == TokenType.SHORT;
  }

  @Override
  public String toString() {
    return tokenType.toString() + " isArray = " + isArray;
  }

}
