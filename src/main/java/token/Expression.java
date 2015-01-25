package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class Expression extends Token implements Visitee {

  public ArrayList<Token> children;

  public Expression(ArrayList<Token> children) {
    super("", TokenType.Expression);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
