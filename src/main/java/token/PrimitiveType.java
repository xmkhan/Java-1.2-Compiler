package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class PrimitiveType extends Token {

  public PrimitiveType(ArrayList<Token> children) {
    super("", TokenType.PrimitiveType, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
