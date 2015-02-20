package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class IfThenStatement extends Token {

  public IfThenStatement(ArrayList<Token> children) {
    super("", TokenType.IfThenStatement, children);
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    for (Token token : children) {
      token.acceptReverse(v);
    }
  }
}
