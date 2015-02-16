package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

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
