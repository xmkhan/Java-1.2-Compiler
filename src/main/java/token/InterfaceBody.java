package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class InterfaceBody extends Token implements Visitee {

  public ArrayList<Token> children;

  public InterfaceBody(ArrayList<Token> children) {
    super("", TokenType.InterfaceBody);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
