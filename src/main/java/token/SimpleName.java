package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class SimpleName extends Token implements Visitee {

  public ArrayList<Token> children;

  public SimpleName(ArrayList<Token> children) {
    super("", TokenType.SimpleName);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
