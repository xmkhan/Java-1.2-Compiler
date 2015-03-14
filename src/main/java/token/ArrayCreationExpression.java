package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ArrayCreationExpression extends Token {
  public Expression expression;
  public PrimitiveType primitiveType;
  public Name name;

  public ArrayCreationExpression(ArrayList<Token> children) {
    super("", TokenType.ArrayCreationExpression, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof Expression) {
      expression = (Expression) token;
    } else if (token instanceof PrimitiveType) {
      primitiveType = (PrimitiveType) token;
    } else if (token instanceof Name) {
      name = (Name) token;
    }
  }

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

  public boolean isPrimitiveType() {
    return primitiveType != null;
  }

  public boolean isObjectType() {
    return name != null;
  }
}
