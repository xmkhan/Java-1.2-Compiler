package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ConditionalOrExpression extends Token {

  public ArrayList<Token> children;

  public ConditionalOrExpression(ArrayList<Token> children) {
    super("", TokenType.ConditionalOrExpression);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
