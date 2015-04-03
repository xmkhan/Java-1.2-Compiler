package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class InterfaceType extends Token {
  private ClassOrInterfaceType classOrInterfaceType;

  public InterfaceType(ArrayList<Token> children) {
    super("", TokenType.InterfaceType, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    classOrInterfaceType.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    classOrInterfaceType.acceptReverse(v);
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }

  private void assignType(Token token) {
    if (token instanceof ClassOrInterfaceType) {
      classOrInterfaceType = (ClassOrInterfaceType) token;
    }
  }

  public Token getType() {
    return classOrInterfaceType != null ? classOrInterfaceType.getType() : null;
  }
}
