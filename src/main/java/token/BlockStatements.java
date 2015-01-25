package token;

import java.util.ArrayList;
import visitor.Visitor;

public class BlockStatements extends Token {

  public ArrayList<Token> children;

  public BlockStatements(ArrayList<Token> children) {
    super("", TokenType.BlockStatements);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
