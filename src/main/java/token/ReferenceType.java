package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class ReferenceType extends Token implements Visitee {

  public ArrayList<Token> children;

  public ReferenceType(ArrayList<Token> children) {
    super("", TokenType.ReferenceType);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
