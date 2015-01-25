package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ExpressionStatement extends Token implements Visitee {

  public ArrayList<Token> children;

  public ExpressionStatement(ArrayList<Token> children) {
    super("", TokenType.ExpressionStatement);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
