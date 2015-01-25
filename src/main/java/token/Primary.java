package token;

import java.util.ArrayList;
import visitor.Visitor;

public class Primary extends Token {

  public ArrayList<Token> children;

  public Primary(ArrayList<Token> children) {
    super("", TokenType.Primary);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
