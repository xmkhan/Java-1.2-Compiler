package symbol;

import token.Token;

/**
 * Class that stores an ASTNode pointer and provides convenience methods for decl types.
 * It provides additional attributes that may be useful later.
 */
public class Symbol {

  private Token token;

  public Symbol(Token token) {
    this.token = token;
  }

  public Token getToken() {
    return token;
  }

}
