package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ClassBodyDeclarations extends Token {

  public ArrayList<ClassBodyDeclaration> bodyDeclarations;

  public ArrayList<ClassBodyDeclaration> getBodyDeclarations() {
    return bodyDeclarations;
  }

  public ClassBodyDeclarations(ArrayList<Token> children) {
    super("", TokenType.ClassBodyDeclarations, children);
    bodyDeclarations = new ArrayList<ClassBodyDeclaration>();
    if (children.get(0) instanceof ClassBodyDeclaration) {
      bodyDeclarations.add((ClassBodyDeclaration) children.get(0));
    } else {
      ClassBodyDeclarations childBodyDeclarations = (ClassBodyDeclarations) children.get(0);
      bodyDeclarations.addAll(childBodyDeclarations.bodyDeclarations);
      bodyDeclarations.add((ClassBodyDeclaration) children.get(1));
    }
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    for (Token token : bodyDeclarations) {
      token.accept(v);
    }
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    for (Token token : bodyDeclarations) {
      token.acceptReverse(v);
    }
  }
}