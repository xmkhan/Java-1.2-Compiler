package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class WhileStatementNoShortIf extends Token {

  public WhileStatementNoShortIf(ArrayList<Token> children) {
    super("", TokenType.WhileStatementNoShortIf, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
