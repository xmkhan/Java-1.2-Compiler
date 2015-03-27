package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ArrayType extends Token {
  public PrimitiveType primitiveType;
  public Name name;

  public ArrayType(ArrayList<Token> children) {
    super("", TokenType.ArrayType, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    for (Token token : children) {
      token.acceptReverse(v);
    }
  }
  private void assignType(Token token) {
    if (token instanceof PrimitiveType) {
      primitiveType = (PrimitiveType) token;
    } else if (token instanceof Name) {
      name = (Name) token;
    }
  }

  public Token getType() {
    if (primitiveType !=  null) return primitiveType.getType();
    return name;
  }

  public String getAbsoluteType() {
    if (primitiveType !=  null) return primitiveType.getType().getTokenType().toString();
    return name.getAbsolutePath();
  }
}
