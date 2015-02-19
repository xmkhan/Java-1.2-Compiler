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

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
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
