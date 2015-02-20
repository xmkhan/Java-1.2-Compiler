package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class Assignment extends Token {

  public Assignment(ArrayList<Token> children) {
    super("", TokenType.Assignment, children);
  }

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
