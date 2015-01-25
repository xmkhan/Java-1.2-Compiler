package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class UnaryExpressionNotMinus extends Token implements Visitee {

  public ArrayList<Token> children;

  public UnaryExpressionNotMinus(ArrayList<Token> children) {
    super("", TokenType.UnaryExpressionNotMinus);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
