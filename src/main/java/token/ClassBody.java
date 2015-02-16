package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ClassBody extends Token {

  public ClassBodyDeclarations bodyDeclarations;

  public ClassBody(ArrayList<Token> children) {
    super("", TokenType.ClassBody, children);
    this.bodyDeclarations = (ClassBodyDeclarations) children.get(1);
  }

  public void accept(Visitor v) throws VisitorException {
    bodyDeclarations.accept(v);
    v.visit(this);
  }
}
