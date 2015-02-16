package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class ClassBodyDeclaration extends Token {

  public Token declaration;

  public ClassBodyDeclaration(ArrayList<Token> children) {
    super("", TokenType.ClassBodyDeclaration, children);
    this.declaration = children.get(0);
  }

  public void accept(Visitor v) throws VisitorException {
    declaration.accept(v);
    v.visit(this);
  }
}
