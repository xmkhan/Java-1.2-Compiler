package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

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
