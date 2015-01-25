package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class InclusiveOrExpression extends Token implements Visitee {

  public ArrayList<Token> children;

  public InclusiveOrExpression(ArrayList<Token> children) {
    super("", TokenType.InclusiveOrExpression);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
