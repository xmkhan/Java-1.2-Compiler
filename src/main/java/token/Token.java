package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

/**
 * Interface for implementing a tokenType for the Java AST.
 */
public class Token {
  // position of the token
  protected int tokenStartPosition;
  protected int lineNumber;

  protected String lexeme;
  protected TokenType tokenType;

  public ArrayList<Token> children;

  public Token(String lexeme, TokenType tokenType, ArrayList<Token> children) {
    this.lexeme = lexeme;
    this.tokenType = tokenType;
    this.children = children;
    if (children != null && children.size() > 0) {
      setLocation(children.get(0).getLineNumber(), children.get(0).getTokenStartPosition());
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

  public void setLexeme(String lexeme) {
    this.lexeme = lexeme;
  }

  /**
   * Get the underlying tokenType type.
   */
  public TokenType getTokenType() {
    return tokenType;
  }

  /**
   * Pass the current token to the visitor
   * to perform an post-order traversal.
   */
  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }

  /**
   * Passes the current token to the visitor
   * to perform a pre-order traversal.
   */
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
  }

  /**
   * Allows visitor to control traversal of visitees.
   */
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }

  /**
   * construct an error msg from the location of the token
   */
  public String getErrMsgLocation() {
    return "\nError happened at:\n\tline: " + lineNumber + "\n" +
        "\tstarting character: " + tokenStartPosition + "\n";
  }

  public void setLocation(int lineNumber, int tokenStartPosition) {
    this.lineNumber = lineNumber;
    this.tokenStartPosition = tokenStartPosition;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public int getTokenStartPosition() {
    return tokenStartPosition;
  }
}
