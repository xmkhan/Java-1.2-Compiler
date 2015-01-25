package token;

import java.util.ArrayList;
import visitor.Visitee;
import visitor.Visitor;

public class AssignmentOperator extends Token implements Visitee {

  public ArrayList<Token> children;

  public AssignmentOperator(ArrayList<Token> children) {
    super("", TokenType.AssignmentOperator);
  }

  public void accept(Visitor v) {
    v.visit(this);
  }
}
