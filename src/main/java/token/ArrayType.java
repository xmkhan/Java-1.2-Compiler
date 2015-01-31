package token;

import java.util.ArrayList;
import visitor.Visitor;
import visitor.VisitorException;

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
