package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class AssignmentExpression extends Token {

  public AssignmentExpression(ArrayList<Token> children) {
    super("", TokenType.AssignmentExpression, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
