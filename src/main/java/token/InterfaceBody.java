package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class InterfaceBody extends Token {

  public InterfaceBody(ArrayList<Token> children) {
    super("", TokenType.InterfaceBody, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
