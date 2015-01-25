package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class Assignment extends Token implements Visitee {

  public ArrayList<Token> children;

  public Assignment(ArrayList<Token> children) {
    super("", TokenType.Assignment);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
