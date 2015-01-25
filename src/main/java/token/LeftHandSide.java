package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class LeftHandSide extends Token implements Visitee {

  public ArrayList<Token> children;

  public LeftHandSide(ArrayList<Token> children) {
    super("", TokenType.LeftHandSide);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
