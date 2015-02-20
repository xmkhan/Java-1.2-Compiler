package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class AbstractMethodDeclaration extends Declaration {
  public MethodHeader methodHeader;

  public AbstractMethodDeclaration(ArrayList<Token> children) {
    super("", TokenType.AbstractMethodDeclaration, children);
    methodHeader = (MethodHeader) children.get(0);
    identifier = methodHeader.identifier;
  }

  public void accept(Visitor v) throws VisitorException {
    methodHeader.accept(v);
    v.visit(this);
  }

  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    methodHeader.acceptReverse(v);
  }
}
