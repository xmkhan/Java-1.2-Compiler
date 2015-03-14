package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class FieldAccess extends Token {

  public FieldAccess(ArrayList<Token> children) {
    super("", TokenType.FieldAccess, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  public Primary primary;
  public Token identifier;

  @Override
  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }

  private void assignType(Token token) {
    if (token instanceof Primary) {
      primary = (Primary) token;
    } else if (token.getTokenType() == TokenType.IDENTIFIER) {
      identifier = token;
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
