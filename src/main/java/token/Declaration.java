package token;

import java.util.ArrayList;

/**
 * A base class implementation for all declarations.
 */
public class Declaration extends Token {
  protected Token identifier;

  public Declaration(String lexeme, TokenType tokenType, ArrayList<Token> children) {
    super(lexeme, tokenType, children);
  }

  public String getIdentifier() {
    return identifier.getLexeme();
  }
}
