package token;

import java.util.ArrayList;
import visitor.Visitor;

public class UnaryExpressionNotMinus extends Token {

  public ArrayList<Token> children;

  public UnaryExpressionNotMinus(ArrayList<Token> children) {
    super("", TokenType.UnaryExpressionNotMinus);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
