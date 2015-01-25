package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class BooleanLiteral extends Token implements Visitee {

  public ArrayList<Token> children;

  public BooleanLiteral(ArrayList<Token> children) {
    super("", TokenType.BooleanLiteral);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
