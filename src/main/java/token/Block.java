package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class Block extends Token {

  public Block(ArrayList<Token> children) {
    super("", TokenType.Block, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
