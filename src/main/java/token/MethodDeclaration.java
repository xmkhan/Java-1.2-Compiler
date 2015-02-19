package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class MethodDeclaration extends Declaration {

  public MethodHeader methodHeader;
  public MethodBody methodBody;

  public MethodDeclaration(ArrayList<Token> children) {
    super("", TokenType.MethodDeclaration, children);
    methodHeader = (MethodHeader) children.get(0);
    methodBody = (MethodBody) children.get(1);
    identifier = methodHeader.identifier;
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    methodHeader.accept(v);
    methodBody.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    methodHeader.acceptReverse(v);
    methodBody.acceptReverse(v);
  }
}
