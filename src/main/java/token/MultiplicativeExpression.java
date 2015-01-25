package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class MultiplicativeExpression extends Token implements Visitee {

  public ArrayList<Token> children;

  public MultiplicativeExpression(ArrayList<Token> children) {
    super("", TokenType.MultiplicativeExpression);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
