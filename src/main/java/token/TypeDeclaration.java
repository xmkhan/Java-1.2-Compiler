package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class TypeDeclaration extends Token {

  public ClassDeclaration classDeclaration;
  public InterfaceDeclaration interfaceDeclaration;

  public TypeDeclaration(ArrayList<Token> children) {
    super("", TokenType.TypeDeclaration, children);
    if (children.get(0) instanceof ClassDeclaration) {
      classDeclaration = (ClassDeclaration) children.get(0);
    } else {
      interfaceDeclaration = (InterfaceDeclaration) children.get(0);
    }
  }

  public Declaration getDeclaration() {
    return classDeclaration != null ? classDeclaration : interfaceDeclaration;
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    if (classDeclaration != null) classDeclaration.accept(v);
    else interfaceDeclaration.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    if (classDeclaration != null) classDeclaration.acceptReverse(v);
    else interfaceDeclaration.acceptReverse(v);
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
