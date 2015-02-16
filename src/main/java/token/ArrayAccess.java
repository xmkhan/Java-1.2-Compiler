package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ArrayAccess extends Token {

  public ArrayAccess(ArrayList<Token> children) {
    super("", TokenType.ArrayAccess, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
