package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class InclusiveOrExpression extends Token {

  public ArrayList<Token> children;

  public InclusiveOrExpression(ArrayList<Token> children) {
    super("", TokenType.InclusiveOrExpression);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
