package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class SimpleName extends Token {

  public SimpleName(ArrayList<Token> children) {
    super("", TokenType.SimpleName, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
