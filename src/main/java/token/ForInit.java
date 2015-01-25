package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ForInit extends Token {

  public ArrayList<Token> children;

  public ForInit(ArrayList<Token> children) {
    super("", TokenType.ForInit);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
