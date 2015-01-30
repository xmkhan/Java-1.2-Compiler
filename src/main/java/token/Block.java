package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class Block extends Token {

  public ArrayList<Token> children;

  public Block(ArrayList<Token> children) {
    super("", TokenType.Block);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
