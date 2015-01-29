package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class CastExpression extends Token {

  public ArrayList<Token> children;

  public CastExpression(ArrayList<Token> children) {
    super("", TokenType.CastExpression);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
