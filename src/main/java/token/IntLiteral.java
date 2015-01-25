package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class IntLiteral extends Token implements Visitee {

  public ArrayList<Token> children;

  public IntLiteral(ArrayList<Token> children) {
    super("", TokenType.INT_LITERAL);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
