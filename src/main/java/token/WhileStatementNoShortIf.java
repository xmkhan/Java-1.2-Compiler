package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class WhileStatementNoShortIf extends Token {

  public WhileStatementNoShortIf(ArrayList<Token> children) {
    super("", TokenType.WhileStatementNoShortIf, children);
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
