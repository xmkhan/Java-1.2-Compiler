package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public abstract class BaseIfThenElse extends BaseStatement {
  public Expression expression;
  public StatementNoShortIf statementNoShortIf;

  public BaseIfThenElse(String lexeme, TokenType tokenType, ArrayList<Token> children) {
    super(lexeme, tokenType, children);
    expression = (Expression) children.get(2);
    statementNoShortIf = (StatementNoShortIf) children.get(4);
  }

  public abstract BaseStatement getElseStatement();
}
