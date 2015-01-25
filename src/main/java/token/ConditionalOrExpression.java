package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ConditionalOrExpression extends Token implements Visitee {

  public ArrayList<Token> children;

  public ConditionalOrExpression(ArrayList<Token> children) {
    super("", TokenType.ConditionalOrExpression);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
