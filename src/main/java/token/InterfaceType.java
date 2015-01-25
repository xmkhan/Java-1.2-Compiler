package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class InterfaceType extends Token implements Visitee {

  public ArrayList<Token> children;

  public InterfaceType(ArrayList<Token> children) {
    super("", TokenType.InterfaceType);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
