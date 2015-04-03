package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ForStatementNoShortIf extends BaseForStatement {
  public StatementNoShortIf statement;

  public ForStatementNoShortIf(ArrayList<Token> children) {
    super("", TokenType.ForStatementNoShortIf, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof Statement) {
      statement = (StatementNoShortIf) token;
    }
  }

  @Override
  public Token getStatement() {
    return statement;
  }
}
