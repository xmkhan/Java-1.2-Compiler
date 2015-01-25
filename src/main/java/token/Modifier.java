package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class Modifier extends Token implements Visitee {

  public ArrayList<Token> children;

  public Modifier(ArrayList<Token> children) {
    super("", TokenType.Modifier);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
