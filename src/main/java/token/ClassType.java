package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class ClassType extends Token {

  public ClassType(ArrayList<Token> children) {
    super("", TokenType.ClassType, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
