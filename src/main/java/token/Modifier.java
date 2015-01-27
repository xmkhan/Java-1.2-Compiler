package token;

import java.util.ArrayList;
import visitor.Visitor;

public class Modifier extends Token {

  private Token modifier;

  public Token getModifier() {
    return modifier;
  }

  public Modifier(ArrayList<Token> children) {
    super(children.get(0).getLexeme(), TokenType.Modifier);
    modifier = children.get(0);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
