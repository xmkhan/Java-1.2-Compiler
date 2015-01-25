package token;

import java.util.ArrayList;
import visitor.Visitor;

public class StatementExpression extends Token {

  public ArrayList<Token> children;

  public StatementExpression(ArrayList<Token> children) {
    super("", TokenType.StatementExpression);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}