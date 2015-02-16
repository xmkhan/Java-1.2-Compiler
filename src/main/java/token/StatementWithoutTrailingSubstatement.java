package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class StatementWithoutTrailingSubstatement extends Token {

  public StatementWithoutTrailingSubstatement(ArrayList<Token> children) {
    super("", TokenType.StatementWithoutTrailingSubstatement, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
