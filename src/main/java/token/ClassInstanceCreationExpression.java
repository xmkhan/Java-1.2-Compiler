package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ClassInstanceCreationExpression extends Token {

  public ClassInstanceCreationExpression(ArrayList<Token> children) {
    super("", TokenType.ClassInstanceCreationExpression, children);
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
