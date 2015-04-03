package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class WhileStatementNoShortIf extends BaseWhileStatement {

  public WhileStatementNoShortIf(ArrayList<Token> children) {
    super("", TokenType.WhileStatementNoShortIf, children);
  }
}
