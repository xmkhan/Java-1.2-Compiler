package token;

import java.util.ArrayList;
import visitor.Visitor;

public class CastExpression extends Token {

  public ArrayList<Token> children;

  public CastExpression(ArrayList<Token> children) {
    super("", TokenType.CastExpression);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
