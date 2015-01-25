package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class AndExpression extends Token implements Visitee {

  public ArrayList<Token> children;

  public AndExpression(ArrayList<Token> children) {
    super("",TokenType.AndExpression);
    TokenType type = TokenType.INT;
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
