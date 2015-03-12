package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ArrayAccess extends Token {

  public Name name;
  public Primary primary;
  public Expression expression;

  public ArrayAccess(ArrayList<Token> children) {
    super("", TokenType.ArrayAccess, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof Name) {
      name = (Name) token;
    } else if (token instanceof Expression) {
      expression = (Expression) token;
    } else if (token instanceof Primary) {
      primary = (Primary) token;
    }
  }

  public boolean isAccessOnPrimary() {
    return primary != null;
  }

  public void accept(Visitor v) throws VisitorException {
    if(name != null) name.accept(v);
    if(primary != null) primary.accept(v);
    if(expression != null) expression.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    if(name != null) name.acceptReverse(v);
    if(primary != null) primary.acceptReverse(v);
    if(expression != null) expression.acceptReverse(v);
  }
}
