package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class Expression extends Token {

  public Expression(ArrayList<Token> children) {
    super("", TokenType.Expression, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
