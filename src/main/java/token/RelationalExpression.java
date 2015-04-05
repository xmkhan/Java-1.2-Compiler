package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class RelationalExpression extends Token {

  public RelationalExpression leftExpr;
  public AdditiveExpression rightExpr;
  public ReferenceType referenceType;

  public RelationalExpression(ArrayList<Token> children) {
    super("", TokenType.RelationalExpression, children);

    for(Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof RelationalExpression) {
      leftExpr = (RelationalExpression) token;
    } else if (token instanceof AdditiveExpression) {
      rightExpr = (AdditiveExpression) token;
    } else if (token instanceof ReferenceType) {
      referenceType = (ReferenceType) token;
    }
  }

  public boolean isDefined() {
    return leftExpr != null && (rightExpr != null || referenceType != null);
  }

  public Token getOperator() {
    return children.get(1);
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
