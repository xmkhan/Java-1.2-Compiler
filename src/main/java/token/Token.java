package token;

import visitor.Visitor;
import visitor.VisitorException;

/**
 * Interface for implementing a tokenType for the Java ASTda.
 */
public class Token {
  protected String lexeme;
  protected TokenType tokenType;

  public Token(String lexeme, TokenType tokenType) {
    this.lexeme = lexeme;
    this.tokenType = tokenType;
  }

  /**
   * Gets the underlying lexeme.
   */
  public String getLexeme() {
    return lexeme;
  }

  /**
   * Get the underlying tokenType type.
   */
  public TokenType getTokenType() {
    return tokenType;
  }

  /**
   * Pass the current token to the visitor
   * to perform what it does.
   */
  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
