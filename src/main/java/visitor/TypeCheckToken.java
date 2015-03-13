package visitor;

import token.ClassDeclaration;
import token.Declaration;
import token.TokenType;

public class TypeCheckToken {

  public TokenType tokenType;
  public Declaration declaration;

  public TypeCheckToken(TokenType type) {
    this.tokenType = type;
  }

  public TypeCheckToken (ClassDeclaration token, boolean isArray) {
    if(isArray) {
      this.tokenType = TokenType.ArrayType;
    } else {
      this.tokenType = TokenType.OBJECT;
    }

    this.declaration = token;
  }

}
