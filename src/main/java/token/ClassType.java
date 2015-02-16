package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

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
