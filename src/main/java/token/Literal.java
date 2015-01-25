package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class Literal extends Token implements Visitee {

  public ArrayList<Token> children;

  public Literal(ArrayList<Token> children) {
    super("", TokenType.Literal);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
