package token;

import java.util.ArrayList;
import visitor.Visitor;

public class FieldAccess extends Token {

  public ArrayList<Token> children;

  public FieldAccess(ArrayList<Token> children) {
    super("", TokenType.FieldAccess);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
