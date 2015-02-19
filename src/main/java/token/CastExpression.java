package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class CastExpression extends Token {
  private Name name = null;
  private boolean isExpression;

  public CastExpression(ArrayList<Token> children) {
    super("", TokenType.CastExpression, children);
    isExpression = false;
    checkExpression();
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

  private void checkExpression() {
    Token token = children.get(1);

    if (!(token instanceof Expression)) return;

    isExpression = true;

    while (true) {
      if (token.children == null && !(token instanceof Name) ||
          token.children != null && token.children.size() > 1) {
        break;
      } else if (token instanceof Name) {
        name = (Name) token;
        break;
      }
      token = token.children.get(0);
    }
  }

  public boolean isExpression() {
    return isExpression;
  }

  public boolean isName() {
    return name != null;
  }
}
