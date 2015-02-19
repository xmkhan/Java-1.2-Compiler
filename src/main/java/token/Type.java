package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class Type extends Token {

  public Type(ArrayList<Token> children) {
    super("", TokenType.Type, children);
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
