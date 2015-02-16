package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class EqualityExpression extends Token {

  public EqualityExpression(ArrayList<Token> children) {
    super("", TokenType.EqualityExpression, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
