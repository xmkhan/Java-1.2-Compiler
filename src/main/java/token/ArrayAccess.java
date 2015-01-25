package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ArrayAccess extends Token {

  public ArrayList<Token> children;

  public ArrayAccess(ArrayList<Token> children) {
    super("", TokenType.ArrayAccess);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
