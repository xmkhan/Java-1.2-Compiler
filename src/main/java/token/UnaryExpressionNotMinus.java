package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class UnaryExpressionNotMinus extends Token {


  public Literal literal;
  public Name name;

  public UnaryExpressionNotMinus(ArrayList<Token> children) {
    super("", TokenType.UnaryExpressionNotMinus, children);
    if (isLiteral()) {
      literal = (Literal) ((Primary) children.get(0)).children.get(0);
    } else if (children.get(0) instanceof Name) {
      name = (Name) children.get(0);
    }
  }

  public boolean isLiteral() {
    return children.get(0) instanceof Primary && ((Primary) children.get(0)).children.get(0) instanceof Literal;
  }

  @Override
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
