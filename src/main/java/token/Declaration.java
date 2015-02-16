package token;

import java.util.ArrayList;

/**
 * A base class implementation for all declarations.
 */
public class Declaration extends Token {
  public Declaration(String lexeme, TokenType tokenType, ArrayList<Token> children) {
    super(lexeme, tokenType, children);
  }
}
