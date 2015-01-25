package token;

import java.util.ArrayList;
import visitor.Visitor;

public class Literal extends Token {

  public ArrayList<Token> children;

  public Literal(ArrayList<Token> children) {
    super("", TokenType.Literal);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
