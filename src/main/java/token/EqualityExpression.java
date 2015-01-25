package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class EqualityExpression extends Token implements Visitee {

  public ArrayList<Token> children;

  public EqualityExpression(ArrayList<Token> children) {
    super("", TokenType.EqualityExpression);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
