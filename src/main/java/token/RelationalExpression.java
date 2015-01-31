package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class RelationalExpression extends Token {

  public RelationalExpression(ArrayList<Token> children) {
    super("", TokenType.RelationalExpression, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
