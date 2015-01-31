package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class EqualityExpression extends Token {

  public EqualityExpression(ArrayList<Token> children) {
    super("", TokenType.EqualityExpression, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
