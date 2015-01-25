package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class Type extends Token implements Visitee {

  public ArrayList<Token> children;

  public Type(ArrayList<Token> children) {
    super("", TokenType.Type);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
