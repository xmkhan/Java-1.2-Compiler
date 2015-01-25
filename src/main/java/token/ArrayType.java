package token;

import java.util.ArrayList;
import visitor.Visitor;

public class ArrayType extends Token {

  public ArrayList<Token> children;

  public ArrayType(ArrayList<Token> children) {
    super("", TokenType.ArrayType);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
