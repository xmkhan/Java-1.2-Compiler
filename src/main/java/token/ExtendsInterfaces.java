package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ExtendsInterfaces extends Token {

  public ArrayList<Token> children;

  public ExtendsInterfaces(ArrayList<Token> children) {
    super("", TokenType.ExtendsInterfaces);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
