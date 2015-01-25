package token;

import java.util.ArrayList;
import visitor.Visitor;

public class AssignmentOperator extends Token {

  public ArrayList<Token> children;

  public AssignmentOperator(ArrayList<Token> children) {
    super("", TokenType.AssignmentOperator);
    this.children = children;
  }

  public void accept(Visitor v) {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}