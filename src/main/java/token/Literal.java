package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class Literal extends Token {

  public ArrayList<Token> children;

  public IntLiteral intLiteral;
  public CharLiteral charLiteral;
  public StringLiteral stringLiteral;
  public BooleanLiteral booleanLiteral;
  public Token nullLiteral;

  public Literal(ArrayList<Token> children) {
    super("", TokenType.Literal);
    assignType(children.get(0));
  }

  private void assignType(Token token) {
    if (token instanceof IntLiteral) {
      intLiteral = (IntLiteral) token;
    } else if (token instanceof CharLiteral) {
      charLiteral = (CharLiteral) token;
    } else if (token instanceof StringLiteral) {
      stringLiteral = (StringLiteral) token;
    } else if (token instanceof BooleanLiteral) {
      booleanLiteral = (BooleanLiteral) token;
    } else if (token.getTokenType() == TokenType.NULL) {
      nullLiteral = token;
    }
  }

  public boolean isIntLiteral() {
    return intLiteral != null;
  }

  public boolean isCharLiteral() {
    return charLiteral != null;
  }

  public boolean isStringLiteral() {
    return stringLiteral != null;
  }

  public boolean isBooleanLiteral() {
    return booleanLiteral != null;
  }
  public boolean isNullLiteral() {
    return nullLiteral != null;
  }

  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
