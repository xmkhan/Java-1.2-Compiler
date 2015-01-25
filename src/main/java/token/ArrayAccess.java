package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ArrayAccess extends Token implements Visitee {

  public ArrayList<Token> children;

  public ArrayAccess(ArrayList<Token> children) {
    super("", TokenType.ArrayAccess);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
