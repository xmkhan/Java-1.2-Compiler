package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

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
