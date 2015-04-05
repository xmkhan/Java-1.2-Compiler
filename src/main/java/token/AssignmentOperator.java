package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class AssignmentOperator extends Token {

  public AssignmentOperator(ArrayList<Token> children) {
    super("", TokenType.AssignmentOperator, children);
  }

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
