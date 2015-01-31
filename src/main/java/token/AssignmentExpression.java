package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

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
