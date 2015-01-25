package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class AssignmentExpression extends Token implements Visitee {

  public ArrayList<Token> children;

  public AssignmentExpression(ArrayList<Token> children) {
    super("", TokenType.AssignmentExpression);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
