package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class PackageDeclaration extends Declaration {
  public PackageDeclaration(ArrayList<Token> children) {
    super(children.get(1).getLexeme() != null ? children.get(1).getLexeme() : "", TokenType.PackageDeclaration, children);
    identifier = children.get(1);
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
