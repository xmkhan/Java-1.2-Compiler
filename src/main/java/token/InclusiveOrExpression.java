package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class InclusiveOrExpression extends Token {

  public InclusiveOrExpression leftExpr;
  public AndExpression rightExpr;

  public InclusiveOrExpression(ArrayList<Token> children) {
    super("", TokenType.InclusiveOrExpression, children);

    for(Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof InclusiveOrExpression) {
      leftExpr = (InclusiveOrExpression) token;
    } else if (token instanceof AndExpression) {
      rightExpr = (AndExpression) token;
    }
  }

  public boolean isDefined() {
    return leftExpr != null && rightExpr != null;
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
