package token;

import java.util.ArrayList;
import visitor.Visitor;

public class MethodBody extends Token {

  public ArrayList<Token> children;

  public MethodBody(ArrayList<Token> children) {
    super("", TokenType.MethodBody);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
