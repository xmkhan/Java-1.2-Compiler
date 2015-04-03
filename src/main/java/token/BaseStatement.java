package token;

import java.util.ArrayList;

/**
 * Represents a base elseStatement, that encapsulates in/out reachable.
 */
public class BaseStatement extends Token {

  public boolean in = true;
  public boolean out = true;

  public BaseStatement(String lexeme, TokenType tokenType, ArrayList<Token> children) {
    super(lexeme, tokenType, children);
  }

  public BaseStatement(String lexeme, TokenType tokenType) {
    super(lexeme, tokenType);
  }
}
