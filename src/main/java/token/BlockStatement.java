package token;

import java.util.ArrayList;
import visitor.Visitor;

public class BlockStatement extends Token {

  public ArrayList<Token> children;

  public BlockStatement(ArrayList<Token> children) {
    super("", TokenType.BlockStatement);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
