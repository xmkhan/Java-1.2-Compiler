package token;

import java.util.ArrayList;
import visitor.Visitor;

public class Modifiers extends Token {

  public ArrayList<Token> children;

  public Modifiers(ArrayList<Token> children) {
    super("", TokenType.Modifiers);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
