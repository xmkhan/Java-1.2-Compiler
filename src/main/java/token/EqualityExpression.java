package token;

import java.util.ArrayList;
import visitor.Visitor;

public class EqualityExpression extends Token {

  public ArrayList<Token> children;

  public EqualityExpression(ArrayList<Token> children) {
    super("", TokenType.EqualityExpression);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
