package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class UnaryExpressionNotMinus extends Token {


  public Literal literal;

  public UnaryExpressionNotMinus(ArrayList<Token> children) {
    super("", TokenType.UnaryExpressionNotMinus, children);
    if (isLiteral()) {
      literal = (Literal) ((Primary) children.get(0)).children.get(0);
    }
  }

  public boolean isLiteral() {
    return children.get(0) instanceof Primary && ((Primary) children.get(0)).children.get(0) instanceof Literal;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
