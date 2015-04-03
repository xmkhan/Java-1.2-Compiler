package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class IfThenElseStatementNoShortIf extends BaseIfThenElse {
  public StatementNoShortIf elseStatement;

  public IfThenElseStatementNoShortIf(ArrayList<Token> children) {
    super("", TokenType.IfThenElseStatementNoShortIf, children);
    statementNoShortIf = (StatementNoShortIf) children.get(6);
  }

  @Override
  public BaseStatement getElseStatement() {
    return elseStatement;
  }
}
