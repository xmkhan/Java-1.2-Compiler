package token;

import java.util.ArrayList;
import visitor.Visitor;

public class RelationalExpression extends Token {

  public ArrayList<Token> children;

  public RelationalExpression(ArrayList<Token> children) {
    super("", TokenType.RelationalExpression);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
