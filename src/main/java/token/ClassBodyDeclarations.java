package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
    Collections.sort(bodyDeclarations, new Comparator<ClassBodyDeclaration>() {
      @Override
      public int compare(ClassBodyDeclaration o1, ClassBodyDeclaration o2) {
          int score1 = memberScore(o1);
          int score2 = memberScore(o2);
          if (score1 == score2) {
            return new Integer(bodyDeclarations.indexOf(o1)).compareTo(bodyDeclarations.indexOf(o2));
          } else {
            return new Integer(score1).compareTo(score2);
          }
      }
    });
}

  private int memberScore(ClassBodyDeclaration bodyDeclaration) {
    int i = 0;
    if (bodyDeclaration.declaration instanceof ClassMemberDeclaration &&
        bodyDeclaration.declaration.children.get(0) instanceof MethodDeclaration) i = 1;
    else if (bodyDeclaration.declaration instanceof ConstructorDeclaration) i = 2;
    return i;
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    for (ClassBodyDeclaration token : bodyDeclarations) {
      token.accept(v);
    }
    v.visit(this);
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    for (ClassBodyDeclaration token : bodyDeclarations) {
      token.acceptReverse(v);
    }
  }

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }
}