package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class BlockStatements extends Token {

  public BlockStatements(ArrayList<Token> children) {
    super("", TokenType.BlockStatements, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
