package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class EmptyStatement extends Token {

  public EmptyStatement(ArrayList<Token> children) {
    super("", TokenType.EmptyStatement, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
