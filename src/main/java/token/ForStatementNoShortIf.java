package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

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
