package token;

import java.util.ArrayList;
import visitor.Visitor;

public class TypeDeclarations extends Token {

  public ArrayList<Token> children;

  public TypeDeclarations(ArrayList<Token> children) {
    super("", TokenType.TypeDeclarations);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
