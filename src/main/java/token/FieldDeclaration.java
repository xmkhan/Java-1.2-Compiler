package token;

import java.util.ArrayList;
import visitor.Visitor;

public class FieldDeclaration extends Token {

  public ArrayList<Token> children;

  public FieldDeclaration(ArrayList<Token> children) {
    super("", TokenType.FieldDeclaration);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
