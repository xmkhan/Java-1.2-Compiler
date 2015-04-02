package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class AndExpression extends Token {

  public AndExpression leftExpr;
  public EqualityExpression rightExpr;

  public AndExpression(ArrayList<Token> children) {
    super("", TokenType.AndExpression, children);

    for(Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof AndExpression) {
      leftExpr = (AndExpression) token;
    } else if (token instanceof EqualityExpression) {
      rightExpr = (EqualityExpression) token;
    }
  }

  public boolean isDefined() {
    return leftExpr != null && rightExpr != null;
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
}
