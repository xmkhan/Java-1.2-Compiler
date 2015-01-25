package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class AdditiveExpression extends Token implements Visitee {

  public ArrayList<Token> children;

  public AdditiveExpression(ArrayList<Token> children) {
    super("", TokenType.AdditiveExpression);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
