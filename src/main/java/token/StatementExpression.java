package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class StatementExpression extends Token {

  public StatementExpression(ArrayList<Token> children) {
    super("", TokenType.StatementExpression, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
