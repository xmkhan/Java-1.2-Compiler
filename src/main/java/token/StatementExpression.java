package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class StatementExpression extends Token implements Visitee {

  public ArrayList<Token> children;

  public StatementExpression(ArrayList<Token> children) {
    super("", TokenType.StatementExpression);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
