package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

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

  public void accept(Visitor v) throws VisitorException {
    if (expr1 != null) expr1.accept(v);
    expr.accept(v);
    v.visit(this);
  }
}
