package token;

/**
 * Interface for implementing a tokenType for the Java ASTda.
 */
public class Token {
  private final String lexeme;
  private final TokenType tokenType;

  public Token(String lexeme, TokenType tokenType) {
    this.lexeme = lexeme;
    this.tokenType = tokenType;
  }

  /**
   * Gets the underlying lexeme.
   */
  String getLexeme() {
    return lexeme;
  }

  /**
   * Get the underlying tokenType type.
   */
  TokenType getTokenType() {
    return tokenType;
  }
}
