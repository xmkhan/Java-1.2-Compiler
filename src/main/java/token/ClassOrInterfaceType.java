package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ClassOrInterfaceType extends Token {
  Name name;

  public ClassOrInterfaceType(ArrayList<Token> children) {
    super("", TokenType.ClassOrInterfaceType, children);
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
    return name != null ? name.getSimpleName() : null;
  }

  private void assignType(Token token) {
    if (token instanceof Name) {
      name = (Name) token;
    }
  }
}
