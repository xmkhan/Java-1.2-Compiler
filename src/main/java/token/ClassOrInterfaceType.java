package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ClassOrInterfaceType extends Token {

  public ClassOrInterfaceType(ArrayList<Token> children) {
    super("", TokenType.ClassOrInterfaceType, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
