package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class Assignment extends Token {

  public Assignment(ArrayList<Token> children) {
    super("", TokenType.Assignment, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
