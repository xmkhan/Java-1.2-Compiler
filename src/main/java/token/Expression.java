package token;

import java.util.ArrayList;
import visitor.Visitor;

public class Expression extends Token {

  public ArrayList<Token> children;

  public Expression(ArrayList<Token> children) {
    super("", TokenType.Expression);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
