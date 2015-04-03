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

  public abstract BaseStatement getElseStatement();
}
