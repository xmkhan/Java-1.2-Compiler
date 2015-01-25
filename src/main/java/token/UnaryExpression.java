package token;

import java.util.ArrayList;
import visitor.Visitor;

public class UnaryExpression extends Token {

  public ArrayList<Token> children;

  public UnaryExpression(ArrayList<Token> children) {
    super("", TokenType.UnaryExpression);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
