package token;

import java.util.ArrayList;
import visitor.Visitor;

public class VariableDeclarator extends Token {

  public ArrayList<Token> children;

  public VariableDeclarator(ArrayList<Token> children) {
    super("", TokenType.VariableDeclarator);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
