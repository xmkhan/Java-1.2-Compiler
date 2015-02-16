package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class FieldAccess extends Token {

  public FieldAccess(ArrayList<Token> children) {
    super("", TokenType.FieldAccess, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
