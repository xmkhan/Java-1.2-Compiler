package token;

import java.util.ArrayList;
import visitor.Visitor;

public class AssignmentExpression extends Token {

  public ArrayList<Token> children;

  public AssignmentExpression(ArrayList<Token> children) {
    super("", TokenType.AssignmentExpression);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
