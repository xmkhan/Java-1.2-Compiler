package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class Name extends Token implements Visitee {

  public ArrayList<Token> children;

  public Name(ArrayList<Token> children) {
    super("", TokenType.Name);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
