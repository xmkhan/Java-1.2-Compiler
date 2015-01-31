package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class BlockStatement extends Token {

  public BlockStatement(ArrayList<Token> children) {
    super("", TokenType.BlockStatement, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
