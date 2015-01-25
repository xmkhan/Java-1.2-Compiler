package token;

import java.util.ArrayList;
import visitor.Visitor;

public class Interfaces extends Token {

  public ArrayList<Token> children;

  public Interfaces(ArrayList<Token> children) {
    super("", TokenType.Interfaces);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
