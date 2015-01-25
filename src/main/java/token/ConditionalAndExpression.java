package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ConditionalAndExpression extends Token {

  public ArrayList<Token> children;

  public ConditionalAndExpression(ArrayList<Token> children) {
    super("", TokenType.ConditionalAndExpression);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
