package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class StringLiteral extends Token implements Visitee {

  public ArrayList<Token> children;

  public StringLiteral(ArrayList<Token> children) {
    super("", TokenType.STR_LITERAL);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
