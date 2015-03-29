package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class CastExpression extends Token {
  public Name name = null;
  public PrimitiveType primitiveType = null;
  public Expression expression = null;
  private boolean isArray;

  public CastExpression(ArrayList<Token> children) {
    super("", TokenType.CastExpression, children);
    isArray = children.size() == 6;
    determineCast();
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

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }

  private void determineCast() {
    Token token = children.get(1);

    if(token instanceof Name) {
      name = (Name) token;
    } else if(token instanceof PrimitiveType) {
      primitiveType = (PrimitiveType) token;
    } else if(token instanceof Expression) {
      expression = (Expression) token;

      while (true) {
        if (token.children == null && !(token instanceof Name) ||
                token.children != null && token.children.size() > 1) {
          break;
        } else if (token instanceof Name) {
          name = (Name) token;
          break;
        }
        token = token.children.get(0);
      }
    }
  }

  public boolean isExpression() {
    return expression != null;
  }

  public boolean isName() {
    return name != null;
  }

  public boolean isPrimitiveType() {
    return primitiveType != null;
  }

  public boolean isArrayCast() {
    return isArray;
  }

  @Override
  public String toString() {
    if(isName()) {
      return name.getName().getLexeme() + (isArray ? "[]" : "");
    } else {
      return primitiveType.getType().getTokenType() +  (isArray ? "[]" : "");
    }
  }
}
