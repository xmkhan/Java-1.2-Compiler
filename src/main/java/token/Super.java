package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class Super extends Token implements Visitee {

  public ArrayList<Token> children;

  public Super(ArrayList<Token> children) {
    super("", TokenType.Super);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
