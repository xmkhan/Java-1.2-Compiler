package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class InclusiveOrExpression extends Token {

  public InclusiveOrExpression(ArrayList<Token> children) {
    super("", TokenType.InclusiveOrExpression, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
