package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ConditionalAndExpression extends Token {

  public ConditionalAndExpression leftExpr;
  public InclusiveOrExpression rightExpr;

  public ConditionalAndExpression(ArrayList<Token> children) {
    super("", TokenType.ConditionalAndExpression, children);

    for(Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof ConditionalAndExpression) {
      leftExpr = (ConditionalAndExpression) token;
    } else if (token instanceof InclusiveOrExpression) {
      rightExpr = (InclusiveOrExpression) token;
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
