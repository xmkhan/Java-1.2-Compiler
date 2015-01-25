package token;

import java.util.ArrayList;
import visitor.Visitor;

public class TypeDeclaration extends Token {

  public ArrayList<Token> children;

  public TypeDeclaration(ArrayList<Token> children) {
    super("", TokenType.TypeDeclaration);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
