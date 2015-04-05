package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class Assignment extends Token {

  public LeftHandSide leftHandSide;
  public AssignmentExpression assignmentExpression;

  public Assignment(ArrayList<Token> children) {
    super("", TokenType.Assignment, children);
    for (Token token : children) {
      assignType(token);
    }
  }

  private void assignType(Token token) {
    if (token instanceof LeftHandSide) {
      leftHandSide = (LeftHandSide) token;
    } else if (token instanceof AssignmentExpression) {
      assignmentExpression = (AssignmentExpression) token;
    }
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
