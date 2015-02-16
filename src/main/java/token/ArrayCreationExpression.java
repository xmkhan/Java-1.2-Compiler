package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class ArrayCreationExpression extends Token {

  public ArrayCreationExpression(ArrayList<Token> children) {
    super("", TokenType.ArrayCreationExpression, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
