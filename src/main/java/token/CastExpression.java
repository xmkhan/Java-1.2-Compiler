package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class CastExpression extends Token implements Visitee {

  public ArrayList<Token> children;

  public CastExpression(ArrayList<Token> children) {
    super("", TokenType.CastExpression);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
