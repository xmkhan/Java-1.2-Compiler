package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class RelationalExpression extends Token implements Visitee {

  public ArrayList<Token> children;

  public RelationalExpression(ArrayList<Token> children) {
    super("", TokenType.RelationalExpression);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
