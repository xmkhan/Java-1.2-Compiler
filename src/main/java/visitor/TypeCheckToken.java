package visitor;

import token.ClassDeclaration;
import token.TokenType;

public class TypeCheckToken {

  public TokenType tokenType;
  public ClassDeclaration classDeclaration;

  public TypeCheckToken(TokenType type) {
    this.tokenType = type;
  }

  public TypeCheckToken (ClassDeclaration token) {
    this.tokenType = TokenType.OBJECT;
    this.classDeclaration = token;
  }

}
