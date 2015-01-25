package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ForUpdate extends Token implements Visitee {

  public ArrayList<Token> children;

  public ForUpdate(ArrayList<Token> children) {
    super("", TokenType.ForUpdate);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
