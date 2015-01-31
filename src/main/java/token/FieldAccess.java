package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

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
