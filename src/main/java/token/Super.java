package token;

import java.util.ArrayList;
import visitor.Visitor;

public class Super extends Token {

  public ArrayList<Token> children;

  public Super(ArrayList<Token> children) {
    super("", TokenType.Super);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
