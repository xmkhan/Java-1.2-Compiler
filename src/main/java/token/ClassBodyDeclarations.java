package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class ClassBodyDeclarations extends Token {

  public ArrayList<ClassBodyDeclaration> bodyDeclarations;

  public ArrayList<ClassBodyDeclaration> getBodyDeclarations() {
    return bodyDeclarations;
  }

  public ClassBodyDeclarations(ArrayList<Token> children) {
    super("", TokenType.ClassBodyDeclarations, children);
    bodyDeclarations = new ArrayList<ClassBodyDeclaration>();
    if (children.get(0) instanceof ClassBodyDeclaration) {
      lexeme = children.get(0).getLexeme();
      bodyDeclarations.add((ClassBodyDeclaration) children.get(0));
    } else {
      ClassBodyDeclarations childBodyDeclarations = (ClassBodyDeclarations) children.get(0);
      bodyDeclarations.addAll(childBodyDeclarations.bodyDeclarations);
      bodyDeclarations.add((ClassBodyDeclaration) children.get(1));
    }
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : bodyDeclarations) {
      token.accept(v);
    }
    v.visit(this);
  }
}