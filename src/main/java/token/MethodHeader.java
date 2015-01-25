package token;

import java.util.ArrayList;
import visitor.Visitor;

public class MethodHeader extends Token {

  public ArrayList<Token> children;

  public MethodHeader(ArrayList<Token> children) {
    super("", TokenType.MethodHeader);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
