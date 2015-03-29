package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ClassBodyDeclaration extends Token {

  public Token declaration;

  public ClassBodyDeclaration(ArrayList<Token> children) {
    super("", TokenType.ClassBodyDeclaration, children);
    this.declaration = children.get(0);
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    declaration.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    declaration.acceptReverse(v);
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }

  public boolean isMethod() {
    return declaration.children.get(0) instanceof MethodDeclaration;
  }

  public boolean isConstructor() {
    return declaration instanceof ConstructorDeclaration;
  }

  public boolean isField() {
    return declaration.children.get(0) instanceof FieldDeclaration;
  }
}
