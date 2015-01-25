package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ForInit extends Token implements Visitee {

  public ArrayList<Token> children;

  public ForInit(ArrayList<Token> children) {
    super("", TokenType.ForInit);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
