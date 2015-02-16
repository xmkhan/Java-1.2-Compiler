package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class AdditiveExpression extends Token {

  public AdditiveExpression(ArrayList<Token> children) {
    super("", TokenType.AdditiveExpression, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
