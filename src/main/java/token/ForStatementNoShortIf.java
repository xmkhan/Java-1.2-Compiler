package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class ForStatementNoShortIf extends Token {

  public ForStatementNoShortIf(ArrayList<Token> children) {
    super("", TokenType.ForStatementNoShortIf, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
