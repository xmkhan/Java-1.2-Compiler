package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class FieldAccess extends Token implements Visitee {

  public ArrayList<Token> children;

  public FieldAccess(ArrayList<Token> children) {
    super("", TokenType.FieldAccess);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
