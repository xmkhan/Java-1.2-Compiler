package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class Primary extends Token implements Visitee {

  public ArrayList<Token> children;

  public Primary(ArrayList<Token> children) {
    super("", TokenType.Primary);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
