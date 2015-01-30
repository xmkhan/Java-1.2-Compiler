package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

public class AssignmentExpression extends Token {

  public ArrayList<Token> children;

  public AssignmentExpression(ArrayList<Token> children) {
    super("", TokenType.AssignmentExpression);
    this.children = children;
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
