package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class Statement extends Token {

  public Statement(ArrayList<Token> children) {
    super("", TokenType.Statement, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
