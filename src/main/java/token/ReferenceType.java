package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ReferenceType extends Token {

  public ReferenceType(ArrayList<Token> children) {
    super("", TokenType.ReferenceType, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
