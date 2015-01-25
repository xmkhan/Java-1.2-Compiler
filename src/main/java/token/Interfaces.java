package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class Interfaces extends Token implements Visitee {

  public ArrayList<Token> children;

  public Interfaces(ArrayList<Token> children) {
    super("", TokenType.Interfaces);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
