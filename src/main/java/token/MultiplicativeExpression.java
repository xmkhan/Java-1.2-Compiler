package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class MultiplicativeExpression extends Token {

  public MultiplicativeExpression expr1;
  public Token operator;
  public UnaryExpression expr;


  public MultiplicativeExpression(ArrayList<Token> children) {
    super("", TokenType.MultiplicativeExpression, children);
    if (children.size() == 3) {
      expr1 = (MultiplicativeExpression) children.get(0);
      operator = children.get(1);
      expr = (UnaryExpression) children.get(2);
    } else {
      expr = (UnaryExpression) children.get(0);
    }
  }

  public boolean isDefined() {
    return expr1 != null && expr != null;
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    if (expr1 != null) expr1.accept(v);
    expr.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    if (expr1 != null) expr1.acceptReverse(v);
    expr.acceptReverse(v);
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
