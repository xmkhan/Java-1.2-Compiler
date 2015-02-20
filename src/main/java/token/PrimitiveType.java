package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class PrimitiveType extends Token {

  public PrimitiveType(ArrayList<Token> children) {
    super("", TokenType.PrimitiveType, children);
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

  public Token getType() {
    return children.get(0);
  }
}
