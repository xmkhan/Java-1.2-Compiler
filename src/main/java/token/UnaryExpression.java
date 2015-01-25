package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class UnaryExpression extends Token implements Visitee {

  public ArrayList<Token> children;

  public UnaryExpression(ArrayList<Token> children) {
    super("", TokenType.UnaryExpression);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
