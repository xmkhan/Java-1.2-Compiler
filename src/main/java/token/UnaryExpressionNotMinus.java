package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class UnaryExpressionNotMinus extends Token {

  public Primary primary;
  public Literal literal;
  public Name name;
  public UnaryExpression unaryExpression;
  public CastExpression castExpression;

  public UnaryExpressionNotMinus(ArrayList<Token> children) {
    super("", TokenType.UnaryExpressionNotMinus, children);

    for(Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof Primary) {
      primary = (Primary) token;

      if (isLiteral()) {
        literal = (Literal) primary.children.get(0);
      }
    } else if (token instanceof Name) {
      name = (Name) token;
    } else if (token instanceof UnaryExpression) {
      unaryExpression = (UnaryExpression) token;
    } else if (token instanceof CastExpression) {
      castExpression = (CastExpression) token;
    }
  }

  public boolean isLiteral() {
    return children.get(0) instanceof Primary && ((Primary) children.get(0)).children.get(0) instanceof Literal;
  }

  public boolean isNeg() {
    return children.size() == 2;
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

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
