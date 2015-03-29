package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ConstructorDeclaration extends BaseMethodDeclaration {
  public Modifiers modifiers;
  public ConstructorDeclarator declarator;
  public ConstructorBody body;

  public Token newScope;
  public Token closeScope;

  public ConstructorDeclaration(ArrayList<Token> children) {
    super("", TokenType.ConstructorDeclaration, children);
    for (Token child : children) {
      assignType(child);
    }
    identifier = declarator.children.get(0);
    // To handle implicit scopes, we explicitly add the scope.
    newScope = new Token("{", TokenType.LEFT_BRACE);
    closeScope = new Token("}", TokenType.RIGHT_BRACE);
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
    newScope.accept(v);
    for (Token token : children) {
      token.accept(v);
    }
    closeScope.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    newScope.acceptReverse(v);
    for (Token token : children) {
      token.acceptReverse(v);
    }
    closeScope.acceptReverse(v);
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
