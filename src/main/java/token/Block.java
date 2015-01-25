package token;

import java.util.ArrayList;
import visitor.Visitor;

public class Block extends Token {

  public ArrayList<Token> children;

  public Block(ArrayList<Token> children) {
    super("", TokenType.Block);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
