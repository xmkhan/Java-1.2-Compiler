package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class InterfaceType extends Token {

  public InterfaceType(ArrayList<Token> children) {
    super("", TokenType.InterfaceType, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
