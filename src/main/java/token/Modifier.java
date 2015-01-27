package token;

import java.util.ArrayList;
import visitor.Visitor;

public class Modifier extends Token {

  private Token type;

  public Token getType() {
    return type;
  }
  public Modifier(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.Modifier);
    type = children.get(0);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
