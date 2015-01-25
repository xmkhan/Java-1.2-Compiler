package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ConstructorDeclaration extends Token {

  public ArrayList<Token> children;

  public ConstructorDeclaration(ArrayList<Token> children) {
    super("", TokenType.ConstructorDeclaration);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
