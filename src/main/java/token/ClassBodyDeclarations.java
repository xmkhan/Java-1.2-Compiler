package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ClassBodyDeclarations extends Token {

  public ArrayList<Token> children;

  public ClassBodyDeclarations(ArrayList<Token> children) {
    super("", TokenType.ClassBodyDeclarations);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
