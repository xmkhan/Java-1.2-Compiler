package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class MethodDeclarator extends Token {

  public ArrayList<Token> children;

  public MethodDeclarator(ArrayList<Token> children) {
    super("", TokenType.MethodDeclarator);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
