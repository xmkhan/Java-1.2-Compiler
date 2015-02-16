package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

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
}
