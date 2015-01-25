package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ExpressionStatement extends Token {

  public ArrayList<Token> children;

  public ExpressionStatement(ArrayList<Token> children) {
    super("", TokenType.ExpressionStatement);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
