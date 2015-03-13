package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class AbstractMethodDeclaration extends Declaration {
  public MethodHeader methodHeader;

  public Token newScope;
  public Token closeScope;

  public AbstractMethodDeclaration(ArrayList<Token> children) {
    super("", TokenType.AbstractMethodDeclaration, children);
    methodHeader = (MethodHeader) children.get(0);
    identifier = methodHeader.identifier;
    type = methodHeader.type;
    // To handle implicit scopes, we explicitly add the scope.
    newScope = new Token("{", TokenType.LEFT_BRACE);
    closeScope = new Token("}", TokenType.RIGHT_BRACE);
  }

  public void accept(Visitor v) throws VisitorException {
    newScope.accept(v);
    methodHeader.accept(v);
    closeScope.accept(v);
    v.visit(this);
  }

  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    newScope.acceptReverse(v);
    methodHeader.acceptReverse(v);
    closeScope.acceptReverse(v);
  }
}
