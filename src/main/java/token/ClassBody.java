package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ClassBody extends Token {

  public ClassBodyDeclarations bodyDeclarations;

  public ClassBody(ArrayList<Token> children) {
    super("", TokenType.ClassBody, children);
    if (children.get(1) instanceof ClassBodyDeclarations) {
      this.bodyDeclarations = (ClassBodyDeclarations) children.get(1);
    }
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    if (bodyDeclarations != null) bodyDeclarations.accept(v);
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    if (bodyDeclarations != null) bodyDeclarations.acceptReverse(v);
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}
