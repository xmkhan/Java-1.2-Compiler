package token;

import java.util.ArrayList;

/**
 * Created by ali on 13/03/15.
 */
public class BaseMethodDeclaration extends Declaration {
  public BaseMethodDeclaration(String lexeme, TokenType tokenType, ArrayList<Token> children) {
    super(lexeme, tokenType, children);
  }
}
