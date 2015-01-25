package token;

import java.util.ArrayList;
import visitor.Visitor;

public class MethodDeclarator extends Token {

  public ArrayList<Token> children;

  public MethodDeclarator(ArrayList<Token> children) {
    super("", TokenType.MethodDeclarator);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
