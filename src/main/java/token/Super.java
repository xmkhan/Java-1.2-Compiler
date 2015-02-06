package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class Super extends Token {

  public Super(ArrayList<Token> children) {
    super("", TokenType.Super, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
