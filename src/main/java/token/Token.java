package token;

import visitor.Visitor;
import exception.VisitorException;

import java.util.ArrayList;

/**
 * Interface for implementing a tokenType for the Java ASTda.
 */
public class Token {
  // position of the token
  protected int firstCharPosition;
  protected int lastCharPosition;
  protected int lineCount;

  protected String lexeme;
  protected TokenType tokenType;

  public ArrayList<Token> children;

  public Token(String lexeme, TokenType tokenType, ArrayList<Token> children) {
    this.lexeme = lexeme;
    this.tokenType = tokenType;
    this.children = children;
    if (children != null && children.size() > 0) {
      setLocationInFile(children.get(0).getLineCount(), children.get(0).getFirstCharPosition());
    }
  }

  public Token(String lexeme, TokenType tokenType) {
    this.lexeme = lexeme;
    this.tokenType = tokenType;
    this.children = null;
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

  /**
   * construct an error msg from the location of the token
   */
  public String getErrMsgLocation() {
    return "\nError happened at:\n\tline: " + lineCount + "\n" +
      "\tstarting character: " + firstCharPosition + "\n";
  }

  public void setLocationInFile(int lineCount, int firstCharPosition) {
    this.lineCount = lineCount;
    this.firstCharPosition = firstCharPosition;
  }

  public int getLineCount() {
    return lineCount;
  }

  public int getFirstCharPosition() {
    return firstCharPosition;
  }

  public int getLastCharPosition() {
    return lastCharPosition;
  }
}
