package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class IfThenElseStatementNoShortIf extends Token {

  public IfThenElseStatementNoShortIf(ArrayList<Token> children) {
    super("", TokenType.IfThenElseStatementNoShortIf, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
