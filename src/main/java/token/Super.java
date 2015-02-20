package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class Super extends Token {
  private ClassType classType;

  public Super(ArrayList<Token> children) {
    super("", TokenType.Super, children);
    for (Token token : children) {
      assignType(token);
    }
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

  private void assignType(Token token) {
    if (token instanceof ClassType) {
      classType = (ClassType) token;
    }
  }

  public Token getType() {
    return classType != null ? classType.getType() : null;
  }
}
