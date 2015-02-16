package token;

import exception.VisitorException;
import visitor.Visitor;

import java.util.ArrayList;

public class ArrayType extends Token {

  public ArrayType(ArrayList<Token> children) {
    super("", TokenType.ArrayType, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
