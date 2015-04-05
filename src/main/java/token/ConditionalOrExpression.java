package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ConditionalOrExpression extends Token {

  public ConditionalOrExpression leftExpr;
  public ConditionalAndExpression rightExpr;

  public ConditionalOrExpression(ArrayList<Token> children) {
    super("", TokenType.ConditionalOrExpression, children);

    for(Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof ConditionalOrExpression) {
      leftExpr = (ConditionalOrExpression) token;
    } else if (token instanceof ConditionalAndExpression) {
      rightExpr = (ConditionalAndExpression) token;
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
