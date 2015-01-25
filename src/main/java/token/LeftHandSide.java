package token;

import java.util.ArrayList;
import visitor.Visitor;

public class LeftHandSide extends Token {

  public ArrayList<Token> children;

  public LeftHandSide(ArrayList<Token> children) {
    super("", TokenType.LeftHandSide);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
