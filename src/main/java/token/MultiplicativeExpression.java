package token;

import java.util.ArrayList;
import visitor.Visitor;

public class MultiplicativeExpression extends Token {

  public ArrayList<Token> children;

  public MultiplicativeExpression(ArrayList<Token> children) {
    super("", TokenType.MultiplicativeExpression);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
