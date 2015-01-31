package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

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
