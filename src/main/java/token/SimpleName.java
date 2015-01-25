package token;

import java.util.ArrayList;
import visitor.Visitor;

public class SimpleName extends Token {

  public ArrayList<Token> children;

  public SimpleName(ArrayList<Token> children) {
    super("", TokenType.SimpleName);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
