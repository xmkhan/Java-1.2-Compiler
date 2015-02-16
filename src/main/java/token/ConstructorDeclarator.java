package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ConstructorDeclarator extends Token {

  public ConstructorDeclarator(ArrayList<Token> children) {
    super("", TokenType.ConstructorDeclarator, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
