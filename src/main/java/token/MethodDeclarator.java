package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class MethodDeclarator extends Token {

  public MethodDeclarator(ArrayList<Token> children) {
    super("", TokenType.MethodDeclarator, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
