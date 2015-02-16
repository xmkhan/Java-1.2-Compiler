package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class ForInit extends Token {

  public ForInit(ArrayList<Token> children) {
    super("", TokenType.ForInit, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
