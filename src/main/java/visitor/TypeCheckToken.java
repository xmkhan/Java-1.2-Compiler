package visitor;

import token.*;

public class TypeCheckToken {

  public TokenType tokenType;
  public boolean isArray;
  public Declaration declaration;
  public String absolutePath;
  public boolean isIntLiteral;

  public TypeCheckToken(TokenType type) {
    this.tokenType = type;
  }

  public TypeCheckToken(TokenType type, boolean isArray) {
    this.tokenType = type;
    this.isArray = isArray;
  }

  public TypeCheckToken (Declaration token) {
    this.declaration = token;

    if(token instanceof ClassDeclaration || token instanceof InterfaceDeclaration) {
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
    if(isPrimitiveType()) {
      return null;
    }

    if(absolutePath != null && !absolutePath.isEmpty()) {
      return absolutePath;
    } else if(declaration != null) {
      //TODO: Change to based on declaration instance (use later if ClassDeclaration)
      if(declaration.type != null) {
        return declaration.type.getReferenceName().getAbsolutePath();
      } else {
        return declaration.getAbsolutePath();
      }
    } else {
      return null;
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
    return tokenType.toString() + " isArray = " + isArray + " absolutepath = " + getAbsolutePath();
  }

}
