package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

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

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    for (Token token : children) {
      token.acceptReverse(v);
    }
  }
}
