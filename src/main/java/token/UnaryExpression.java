package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class UnaryExpression extends Token {

  public Token minus;
  public UnaryExpression exp;
  public UnaryExpressionNotMinus posExp;


  public UnaryExpression(ArrayList<Token> children) {
    super("", TokenType.UnaryExpression, children);
    if (children.size() == 2) {
      minus = children.get(0);
      exp = (UnaryExpression) children.get(1);
    } else {
      posExp = (UnaryExpressionNotMinus) children.get(0);
    }
  }


  public boolean isNegative() {
    return minus != null;
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    if (exp != null) exp.accept(v);
    if (posExp != null) posExp.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    if (exp != null) exp.acceptReverse(v);
    if (posExp != null) posExp.acceptReverse(v);
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
