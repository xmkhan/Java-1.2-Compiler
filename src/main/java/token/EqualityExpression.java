package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class EqualityExpression extends Token {

  public EqualityExpression leftExpr;
  public RelationalExpression rightExpr;

  public EqualityExpression(ArrayList<Token> children) {
    super("", TokenType.EqualityExpression, children);

    for(Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof EqualityExpression) {
      leftExpr = (EqualityExpression) token;
    } else if (token instanceof RelationalExpression) {
      rightExpr = (RelationalExpression) token;
    }
  }

  public boolean isDefined() {
    return leftExpr != null && rightExpr != null;
  }

  public boolean isEqualCheck() {
    assert(isDefined());
    return children.get(1).getTokenType() == TokenType.EQUALITY_OP;
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
