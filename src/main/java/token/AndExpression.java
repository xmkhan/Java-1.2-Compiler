package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class AndExpression extends Token {

  public AndExpression(ArrayList<Token> children) {
    super("", TokenType.AndExpression, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
