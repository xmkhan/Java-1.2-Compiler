package token;

import java.util.ArrayList;
import visitor.Visitor;

public class InterfaceType extends Token {

  public ArrayList<Token> children;

  public InterfaceType(ArrayList<Token> children) {
    super("", TokenType.InterfaceType);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
