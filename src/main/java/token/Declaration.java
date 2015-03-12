package token;

import java.util.ArrayList;

/**
 * A base class implementation for all declarations.
 */
public class Declaration extends Token {
  public Token identifier;

  private String absolutePath;

  public String getAbsolutePath() {
    return absolutePath;
  }

  public void setAbsolutePath(String absolutePath) {
    this.absolutePath = absolutePath;
  }

  public Declaration(String lexeme, TokenType tokenType, ArrayList<Token> children) {
    super(lexeme, tokenType, children);
  }

  public String getIdentifier() {
    return identifier.getLexeme();
  }
}
