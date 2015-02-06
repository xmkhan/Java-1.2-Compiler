package token;

import java.util.ArrayList;
import visitor.Visitor;
import exception.VisitorException;

public class Type extends Token {

  public Type(ArrayList<Token> children) {
    super("", TokenType.Type, children);
  }

  public void accept(Visitor v) throws VisitorException {
    for (Token token : children) {
      token.accept(v);
    }
    v.visit(this);
  }
}
