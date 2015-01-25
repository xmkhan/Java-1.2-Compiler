package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class Modifiers extends Token implements Visitee {

  public ArrayList<Token> children;

  public Modifiers(ArrayList<Token> children) {
    super("", TokenType.Modifiers);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
