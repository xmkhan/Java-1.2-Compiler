package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class ConstructorBody extends Token {

  public ConstructorBody(ArrayList<Token> children) {
    super("", TokenType.ConstructorBody, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
