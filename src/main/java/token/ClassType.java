package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ClassType extends Token {
  private ClassOrInterfaceType classOrInterfaceType;

  public ClassType(ArrayList<Token> children) {
    super("", TokenType.ClassType, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }

  public Token getType() {
    return classOrInterfaceType != null ? classOrInterfaceType.getType() : null;
  }

  private void assignType(Token token) {
    if (token instanceof ClassOrInterfaceType) {
      classOrInterfaceType = (ClassOrInterfaceType) token;
    }
  }
}
