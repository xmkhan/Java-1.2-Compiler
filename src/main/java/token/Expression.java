package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class Expression extends Token {

  public Expression(ArrayList<Token> children) {
    super("", TokenType.Expression, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
