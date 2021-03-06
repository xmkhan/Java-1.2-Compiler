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
    if (token instanceof StatementNoShortIf) {
      statement = (StatementNoShortIf) token;
    }
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

  @Override
  public void traverse(Visitor v) throws VisitorException {
    v.visit(this);
  }

  @Override
  public Token getStatement() {
    return statement;
  }
}
