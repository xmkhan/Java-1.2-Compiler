package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ClassDeclaration extends Token {

  public ArrayList<Token> children;

  public ClassDeclaration(ArrayList<Token> children) {
    super("", TokenType.ClassDeclaration);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
