package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class Primary extends Token {

  public Primary(ArrayList<Token> children) {
    super("", TokenType.Primary, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
