package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class MethodBody extends Token {

  public Block block;

  public MethodBody(ArrayList<Token> children) {
    super("", TokenType.MethodBody, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  public boolean isEmpty() {
    return children.get(0).getTokenType() == TokenType.SEMI_COLON;
  }

  @Override
  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }

  private void assignType(Token token) {
    if (token instanceof Block) {
      block = ((Block) token);
    }
  }

  @Override
  public void acceptReverse(Visitor v) throws VisitorException {
    v.visit(this);
    for (Token token : children) {
      token.acceptReverse(v);
    }
  }
}
