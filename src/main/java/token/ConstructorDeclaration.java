package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ConstructorDeclaration extends Declaration {
  public Modifiers modifiers;
  public ConstructorDeclarator declarator;
  public ConstructorBody body;

  public ConstructorDeclaration(ArrayList<Token> children) {
    super("", TokenType.ConstructorDeclaration, children);
    for (Token child : children) {
      assignType(child);
    }
    identifier = declarator.children.get(0);
  }

  public void assignType(Token token) {
    if (token instanceof Modifiers) {
      modifiers = (Modifiers) token;
    } else if (token instanceof ConstructorDeclarator) {
      declarator = (ConstructorDeclarator) token;
    } else if (token instanceof ConstructorBody) {
      body = (ConstructorBody) token;
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
}
