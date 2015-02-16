package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class Literal extends Token {

  public TokenType valueType;

  public Literal(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.Literal, children);
    assignType(children.get(0));
  }

  private void assignType(Token token) {
    valueType = token.getTokenType();
  }

  public boolean isIntLiteral() {
    return valueType == TokenType.INT_LITERAL;
  }

  public boolean isCharLiteral() {
    return valueType == TokenType.CHAR_LITERAL;
  }

  public boolean isStringLiteral() {
    return valueType == TokenType.STR_LITERAL;
  }

  public boolean isBooleanLiteral() {
    return valueType == TokenType.BOOLEAN_LITERAL;
  }

  public boolean isNullLiteral() {
    return valueType == TokenType.NULL;
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
