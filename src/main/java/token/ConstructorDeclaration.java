package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class ConstructorDeclaration extends Token {

  public ConstructorDeclaration(ArrayList<Token> children) {
    super("", TokenType.ConstructorDeclaration, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
