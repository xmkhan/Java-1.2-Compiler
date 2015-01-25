package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class PrimitiveType extends Token implements Visitee {

  public ArrayList<Token> children;

  public PrimitiveType(ArrayList<Token> children) {
    super("", TokenType.PrimitiveType);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
