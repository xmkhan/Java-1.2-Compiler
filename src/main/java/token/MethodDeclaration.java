package token;

import java.util.ArrayList;
import visitor.Visitor;

public class MethodDeclaration extends Token {

  public ArrayList<Token> children;

  public MethodDeclaration(ArrayList<Token> children) {
    super("", TokenType.MethodDeclaration);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
