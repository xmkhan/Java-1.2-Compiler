package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ConstructorBody extends Token {

  public ArrayList<Token> children;

  public ConstructorBody(ArrayList<Token> children) {
    super("", TokenType.ConstructorBody);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
