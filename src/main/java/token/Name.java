package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class Name extends Token {

  public Name(ArrayList<Token> children) {
    super("", TokenType.Name, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
