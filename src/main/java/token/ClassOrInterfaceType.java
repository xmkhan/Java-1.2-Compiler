package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ClassOrInterfaceType extends Token {
  public Name name;

  public ClassOrInterfaceType(ArrayList<Token> children) {
    super("", TokenType.ClassOrInterfaceType, children);
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

  public Token getType() {
    return name;
  }

  private void assignType(Token token) {
    if (token instanceof Name) {
      name = (Name) token;
    }
  }

}
