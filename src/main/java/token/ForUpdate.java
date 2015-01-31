package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class ForUpdate extends Token {

  public ForUpdate(ArrayList<Token> children) {
    super("", TokenType.ForUpdate, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
