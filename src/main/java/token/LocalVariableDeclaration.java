package token;

import java.util.ArrayList;
import visitor.Visitor;

public class LocalVariableDeclaration extends Token {

  public ArrayList<Token> children;

  public LocalVariableDeclaration(ArrayList<Token> children) {
    super("", TokenType.LocalVariableDeclaration);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
