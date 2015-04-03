package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;
import java.util.Stack;

public class IfThenStatement extends BaseStatement {
  public Expression expression;
  public Statement statement;

  public IfThenStatement(ArrayList<Token> children) {
    super("", TokenType.IfThenStatement, children);
    expression = (Expression) children.get(2);
    statement = (Statement) children.get(4);
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
}
