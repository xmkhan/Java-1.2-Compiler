package token;

import java.util.ArrayList;
import visitor.Visitor;

public class AndExpression extends Token {

  public ArrayList<Token> children;

  public AndExpression(ArrayList<Token> children) {
    super("", TokenType.AndExpression);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
