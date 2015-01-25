package token;

import java.util.ArrayList;
import visitor.Visitor;

public class AdditiveExpression extends Token {

  public ArrayList<Token> children;

  public AdditiveExpression(ArrayList<Token> children) {
    super("", TokenType.AdditiveExpression);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
